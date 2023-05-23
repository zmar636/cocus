package com.challenge.cocus.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class GitHubClientConfig {

    @Bean
    fun gitHubWebClient(): WebClient {
        return  WebClient.create("https://api.github.com");
    }
}