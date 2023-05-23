package com.challenge.cocus.external

import com.challenge.cocus.dto.BranchInfoDto
import com.challenge.cocus.dto.Commit
import com.challenge.cocus.dto.Owner
import com.challenge.cocus.dto.RepositoryInfoDto
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux


@ExtendWith(MockitoExtension::class)
class GitHubApiClientTest {

    companion object {
        lateinit var mockBackEnd: MockWebServer

        @BeforeAll
        @JvmStatic
        fun setUp() {
            mockBackEnd = MockWebServer()
            mockBackEnd.start()
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            mockBackEnd.shutdown()
        }
    }

    private lateinit var webClient: WebClient
    private lateinit var gitHubApiClient: GitHubApiClient;

    @BeforeEach
    fun initialize() {
        val baseUrl = String.format(
            "http://localhost:%s",
            mockBackEnd.port
        )
        webClient = WebClient.create(baseUrl)
        gitHubApiClient = GitHubApiClient(webClient)
    }

    @Test
    fun `getUserRepositories should return repositories for a user`() {
        val username = "jose"
        val repositoryInfoDto = RepositoryInfoDto("repo1", Owner("jose"), false,"branchesUrl")
        val branchInfoDto = BranchInfoDto("branch1", Commit("sha1"))

        mockBackEnd.enqueue(
            MockResponse()
                .setBody(jacksonObjectMapper().writeValueAsString(repositoryInfoDto))
                .addHeader("Content-Type", MediaType.APPLICATION_JSON)
        )

        mockBackEnd.enqueue(
            MockResponse().setBody(jacksonObjectMapper().writeValueAsString(branchInfoDto))
                .addHeader("Content-Type", MediaType.APPLICATION_JSON)
        );

        val repositories = gitHubApiClient.getUserRepositories(username);

        assertEquals("repo1", repositories.blockFirst()?.repoName ?: "Wrong repo")
    }

    @Test
    fun `given there are no repositories for user when get repositories return empty list`() {
        val username = "jose"

        mockBackEnd.enqueue(MockResponse().setResponseCode(200));

        val repositories = gitHubApiClient.getUserRepositories(username);

        assertEquals(0, repositories.collectList().block()?.size)
    }

    @Test
    fun `given there are one fork repo and one none fork repo when get repositories then return only none fork `(){

        val repositoryInfoDto = RepositoryInfoDto("repo1", Owner("jose"), false,"branchesUrl")
        val repositoryInfoDtoFork = RepositoryInfoDto("repofork", Owner("jose"), true,"branchesUrl")
        val branchInfoDto = BranchInfoDto("branch1", Commit("sha1"))

        mockBackEnd.enqueue(
            MockResponse()
                .setBody(jacksonObjectMapper().writeValueAsString(listOf(repositoryInfoDto, repositoryInfoDtoFork)))
                .addHeader("Content-Type", MediaType.APPLICATION_JSON)
        )

        mockBackEnd.enqueue(
            MockResponse().setBody(jacksonObjectMapper().writeValueAsString(branchInfoDto))
                .addHeader("Content-Type", MediaType.APPLICATION_JSON)
        )

        val repositories = gitHubApiClient.getUserRepositories("jose");

        val repositoryList = repositories.collectList().block()
        assertEquals(1, repositoryList?.size)
        assertEquals("repo1", repositoryList?.get(0)?.repoName ?: "wrong repo name")

    }

}