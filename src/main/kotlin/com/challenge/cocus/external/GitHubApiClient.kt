package com.challenge.cocus.external

import com.challenge.cocus.dto.BranchInfoDto
import com.challenge.cocus.dto.RepositoryInfoDto
import com.challenge.cocus.exceptions.GitHubClientException
import com.challenge.cocus.exceptions.GitHubClientRepositoriesNotFoundException
import com.challenge.cocus.model.BranchInfo
import com.challenge.cocus.model.RepositoryInfo
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodilessEntity
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.stream.Collectors

const val GIT_HUB_FETCH_EXCEPTION = "Failed to fetch Git Hub data"

@Component
class GitHubApiClient(val client: WebClient) {

    private val logger = LoggerFactory.getLogger(GitHubApiClient::class.java)

    fun getUserRepositories(username: String): Flux<RepositoryInfo> {
        logger.info("Fetching repositories for user: $username")

        return client.get()
            .uri("/users/$username/repos")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(
                { it.isError },
                { Mono.error(GitHubClientException("Failed to fetch repositories for user: $username")) }
            )
            .onStatus(
                { it.isSameCodeAs(HttpStatus.NOT_FOUND) },
                { Mono.error(GitHubClientRepositoriesNotFoundException("No repositories found for user: $username")) })
            .bodyToFlux(RepositoryInfoDto::class.java)
            .filter { !it.fork }
            .flatMapSequential { repositoryInfoDto ->
                fetchBranches(username, repositoryInfoDto.name)
                    .collectList()
                    .map { branch ->
                        RepositoryInfo(
                            repositoryInfoDto.name,
                            repositoryInfoDto.owner.login,
                            branch.map { BranchInfo(it.name, it.commit.sha) })
                    }
            }
    }

    private fun fetchBranches(username: String, repository: String): Flux<BranchInfoDto> {
        logger.info("Fetching branches for repository: $repository of user: $username")
        return client.get()
            .uri("repos/$username/$repository/branches")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(
                { it.isError },
                {
                    it.bodyToMono<Throwable>()
                        .map { GitHubClientException("Failed to fetch branches for repository: $repository") }
                })
            .bodyToFlux(BranchInfoDto::class.java)
    }
}