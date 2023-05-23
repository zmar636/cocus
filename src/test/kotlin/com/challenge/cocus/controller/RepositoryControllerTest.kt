package com.challenge.cocus.controller

import com.challenge.cocus.exceptions.ErrorInfo
import com.challenge.cocus.exceptions.GitHubClientException
import com.challenge.cocus.exceptions.GitHubClientRepositoriesNotFoundException
import com.challenge.cocus.external.GitHubApiClient
import com.challenge.cocus.model.BranchInfo
import com.challenge.cocus.model.RepositoryInfo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@WebFluxTest(RepositoryController::class)
class RepositoryControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var gitHubApiClient: GitHubApiClient

    @Test
    fun `given get repositories when githubclient is ok then return repositories list`() {
        val repositories = listOf(
            RepositoryInfo("jose", "repo1", listOf(BranchInfo("branch1", "sha1"))),
            RepositoryInfo("jose", "repo2", listOf(BranchInfo("branch2", "sha2")))
        )

        given(gitHubApiClient.getUserRepositories("jose")).willReturn(Flux.fromIterable(repositories))

        val response = webTestClient.get()
            .uri("/repositories/jose")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(RepositoryInfo::class.java)
            .returnResult()

        val responseBody = response.responseBody ?: emptyList()
        assertEquals(repositories.size, responseBody.size)
    }

    @Test
    fun `given git hub api exception when get repositories then status is 500` () {
        given(gitHubApiClient.getUserRepositories("jose")).willReturn(
            Flux.error(
                GitHubClientException("Error fetching Git Hub data")
            )
        )

        val response = webTestClient.get()
            .uri("/repositories/jose")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is5xxServerError
            .expectBody(ErrorInfo::class.java)
            .returnResult()

        val errorInfo = response.responseBody ?: ErrorInfo(HttpStatus.OK.value(), "Should return error")
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorInfo.status)
        assertEquals("Error fetching Git Hub data", errorInfo.message)
    }

    @Test
    fun `given repositories not found and accepts json when get repositories return 404`() {

        // Mock the response from GitHubApiClient
        given(gitHubApiClient.getUserRepositories("jose")).willReturn(
            Flux.error(GitHubClientRepositoriesNotFoundException("Repository not found"))
        )

        val response = webTestClient.get()
            .uri("/repositories/jose")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody(ErrorInfo::class.java)
            .returnResult()

        // Verify the response
        val errorInfo = response.responseBody ?: ErrorInfo(HttpStatus.OK.value(), "Should return error")
        assertEquals(HttpStatus.NOT_FOUND.value(), errorInfo.status)
    }

    @Test
    fun `given accepts xml when get repositories return 406`() {
        val response = webTestClient.get()
            .uri("/repositories/jose")
            .accept(MediaType.APPLICATION_XML)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody(ErrorInfo::class.java)
            .returnResult()

        // Verify the response
        val errorInfo = response.responseBody ?: ErrorInfo(HttpStatus.OK.value(), "Should return error")
        assertEquals(HttpStatus.NOT_ACCEPTABLE.value(), errorInfo.status)
    }

}