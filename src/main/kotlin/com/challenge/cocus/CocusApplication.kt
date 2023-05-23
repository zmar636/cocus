package com.challenge.cocus

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient

@OpenAPIDefinition
@SpringBootApplication
class CocusApplication

fun main(args: Array<String>) {
    runApplication<CocusApplication>(*args)
}


@Bean
fun getGitHubClient() :WebClient = WebClient.create("https://api.github.com");
