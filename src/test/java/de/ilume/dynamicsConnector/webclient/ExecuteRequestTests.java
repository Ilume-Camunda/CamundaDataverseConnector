package de.ilume.dynamicsConnector.webclient;

import de.ilume.dynamicsConnector.exception.BadRequestException;
import de.ilume.dynamicsConnector.service.ExecuteRequestService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ExecuteRequestTests {
    private MockWebServer mockWebServer;
    private WebClient webClient;
    private ExecuteRequestService executeRequestService;

    @BeforeEach
    void setUp() throws IOException {
        // Start the MockWebServer
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // Create a WebClient that will use the MockWebServer
        webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        // Initialize the service with the mocked WebClient
        executeRequestService = new ExecuteRequestService(webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        // Shut down the MockWebServer after tests
        mockWebServer.shutdown();
    }

    @Test
    void testGetRequest() {
        // Prepare the mock response
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\n" +
                        "    \"@odata.context\": \"https://camunda.crm16.dynamics.com/api/data/v9.2/$metadata#accounts(name,revenue)\",\n" +
                        "    \"value\": [\n" +
                        "        {\n" +
                        "            \"@odata.etag\": \"W/\\\"7651045\\\"\",\n" +
                        "            \"name\": \"MS Account Info\",\n" +
                        "            \"revenue\": 5000000.0000000000,\n" +
                        "            \"_transactioncurrencyid_value\": \"b3b8dbf1-f819-ef11-840b-002248751737\",\n" +
                        "            \"accountid\": \"f9beedd9-d85a-ef11-bfe2-002248d8793d\"\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}")
                .addHeader("Content-Type", "application/json"));

        String requestUrl = "/api/data/v9.2/accounts?$select=name,revenue&$top=1";
        String accessToken = "fake-token";

        Mono<String> responseMono = executeRequestService.getRequest(requestUrl, accessToken);

        // Validate the response using StepVerifier
        StepVerifier.create(responseMono)
                .expectNext("{\n" +
                        "    \"@odata.context\": \"https://camunda.crm16.dynamics.com/api/data/v9.2/$metadata#accounts(name,revenue)\",\n" +
                        "    \"value\": [\n" +
                        "        {\n" +
                        "            \"@odata.etag\": \"W/\\\"7651045\\\"\",\n" +
                        "            \"name\": \"MS Account Info\",\n" +
                        "            \"revenue\": 5000000.0000000000,\n" +
                        "            \"_transactioncurrencyid_value\": \"b3b8dbf1-f819-ef11-840b-002248751737\",\n" +
                        "            \"accountid\": \"f9beedd9-d85a-ef11-bfe2-002248d8793d\"\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}")
                .verifyComplete();
    }

    @Test
    void testGetBadRequestException() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("Bad Request")
                .addHeader("Content-Type", "text/plain"));

        String requestUrl = "/api/data/v9.2/accounts?$select=name,revenue&$top=1";
        String accessToken = "fake-token";

        Mono<String> responseMono = executeRequestService.getRequest(requestUrl, accessToken);

        StepVerifier.create(responseMono)
                .expectErrorSatisfies(throwable -> {
                    assertThat(throwable).isInstanceOf(BadRequestException.class);
                })
                .verify();
    }
}