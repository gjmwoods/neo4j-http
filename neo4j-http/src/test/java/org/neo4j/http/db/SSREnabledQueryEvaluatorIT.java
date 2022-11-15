/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.http.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.AuthToken;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Logging;
import org.neo4j.driver.Session;
import org.neo4j.junit.jupiter.causal_cluster.CausalCluster;
import org.neo4j.junit.jupiter.causal_cluster.NeedsCausalCluster;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
@NeedsCausalCluster(password = SSREnabledQueryEvaluatorIT.PASSWORD, neo4jVersion = "5.1")
public class SSREnabledQueryEvaluatorIT {

    static final String USERNAME = "neo4j";
    static final String PASSWORD = "bananas";

    @Autowired
    private TestRestTemplate restTemplate;

    @CausalCluster
    private static URI neo4jUri;

    @DynamicPropertySource
    static void prepareNeo4j(DynamicPropertyRegistry registry) {
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", () -> PASSWORD);
        registry.add("spring.neo4j.uri", () -> neo4jUri);
    }

    @Test
    void aTest() {
        var headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_JSON);
        headers.setAccept( List.of( MediaType.APPLICATION_NDJSON));
        var requestEntity = new HttpEntity<>(
                """
                {
                    "statement": "CREATE (n)"
                }""", headers);

        var exchange = this.restTemplate
                .withBasicAuth(USERNAME, PASSWORD)
                .exchange( "/db/neo4j/tx/commit", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<Map<String, String>>() {
                });
        assertThat(exchange.getStatusCode()).isEqualTo( HttpStatus.BAD_REQUEST);
        assertThat(exchange.getBody())
                .containsEntry("message", "MATCH n RETURN n")
                .containsEntry("error", "Invalid query");
    }
}
