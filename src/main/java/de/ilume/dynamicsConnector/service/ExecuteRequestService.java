package de.ilume.dynamicsConnector.service;

import de.ilume.dynamicsConnector.exception.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.http.HttpStatus.*;


/**
 * Defines all API calls that can be made to communicate with the Dataverse Web API and incite
 * changes within the corresponding environment. During the usage of these calls, the method
 * "handleErrorResponse" checks the procedure for any errors defined in its body,
 * aborts it when they happen and returns the result accordingly.
 */

@Service
public class ExecuteRequestService {
    private final WebClient webClient;

    public ExecuteRequestService(WebClient webClient){
        this.webClient = webClient;
    }

    private static final Logger logger = LogManager.getLogger(ExecuteRequestService.class);

    private Mono<? extends Throwable> handleErrorResponse(ClientResponse response) {
        return response.bodyToMono(String.class)
                .flatMap(errorBody -> {
                    return switch (response.statusCode()) {
                        case INTERNAL_SERVER_ERROR -> Mono.error(new InternalServerErrorException(errorBody));
                        case BAD_REQUEST -> Mono.error(new BadRequestException(errorBody));
                        case NOT_FOUND -> Mono.error(new NotFoundException(errorBody));
                        case METHOD_NOT_ALLOWED -> Mono.error(new MethodNotAllowedException(errorBody));
                        default -> Mono.error(new RuntimeException("Unexpected error: " + errorBody));
                    };
                });
    }

    public Mono<String> getRequest(String requestUrl, String accessToken){
        return webClient
                .get()
                .uri(requestUrl)
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleErrorResponse)
                .bodyToMono(String.class)
                .doOnError(throwable -> logger.error("Failed get request", throwable));
    }

    public Mono<String> postRequest(String requestUrl, String accessToken, Map<String, Object> headers, Map<String, Object> requestBody) {
        return webClient
                .post()
                .uri(requestUrl)
                .headers(h -> {
                    h.setBearerAuth(accessToken);
                    headers.forEach((key, value) -> h.add(key, value.toString()));
                })
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleErrorResponse)
                .bodyToMono(String.class)
                .defaultIfEmpty("Request Status Code: 204 No Content")
                .doOnError(throwable -> logger.error("Failed post request", throwable));
    }

    public Mono<String> patchRequest(String requestUrl, String accessToken, Map<String, Object> headers, Map<String, Object> requestBody) {
        return webClient
                .patch()
                .uri(requestUrl)
                .headers(h -> {
                    h.setBearerAuth(accessToken);
                    headers.forEach((key, value) -> h.add(key, value.toString()));
                })
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleErrorResponse)
                .bodyToMono(String.class)
                .defaultIfEmpty("Request Status Code: 204 No Content")
                .doOnError(throwable -> logger.error("Failed patch request", throwable));
    }

    public Mono<String> putRequest(String requestUrl, String accessToken, Map<String, Object> headers, Map<String, Object> requestBody) {
        return webClient
                .put()
                .uri(requestUrl)
                .headers(h -> {
                    h.setBearerAuth(accessToken);
                    headers.forEach((key, value) -> h.add(key, value.toString()));
                })
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleErrorResponse)
                .bodyToMono(String.class)
                .defaultIfEmpty("Request Status Code: 204 No Content")
                .doOnError(throwable -> logger.error("Failed put request", throwable));
    }

    public Mono<String> deleteRequest(String requestUrl, String accessToken, Map<String, Object> headers, Map<String, Object> requestBody) {
        return webClient
                .delete()
                .uri(requestUrl)
                .headers(h -> {
                    h.setBearerAuth(accessToken);
                    headers.forEach((key, value) -> h.add(key, value.toString()));
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleErrorResponse)
                .bodyToMono(String.class)
                .defaultIfEmpty("Request Status Code: 204 No Content")
                .doOnError(throwable -> logger.error("Failed delete request", throwable));
    }
}