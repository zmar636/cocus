package com.challenge.cocus.controller

import com.challenge.cocus.exceptions.NotSupportedMediaTypeException
import com.challenge.cocus.external.GitHubApiClient
import com.challenge.cocus.model.RepositoryInfo
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class RepositoryController(private val gitHubApiClient: GitHubApiClient) {
    @GetMapping("/repositories/{username}")
    fun getRepositories(
        @PathVariable username: String,
        @RequestHeader(name = HttpHeaders.ACCEPT) accept: String
    ): Flux<RepositoryInfo> {
        val mediaTypes = MediaType.parseMediaTypes(accept);

        if (mediaTypes.contains(MediaType.APPLICATION_XML)) {
            return Flux.error(NotSupportedMediaTypeException("Media type \"APPLICATION_XML_VALUE\" is not supported"));
        }
        return gitHubApiClient.getUserRepositories(username);
    }
}