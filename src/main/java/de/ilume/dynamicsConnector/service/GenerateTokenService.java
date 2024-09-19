package de.ilume.dynamicsConnector.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Service Class to generate an access token using the authorization values provided by the user*/
@RequiredArgsConstructor
@Service
public class GenerateTokenService {

    /**
     * Takes authorization values out of a running Camunda process and uses them
     * to send a request for generating an access token. If the request is successful
     * a ResponseEntity is returned which is then converted into a JSON Object
     * before the Token entry is read and used for sending requests directly to the Dataverse Web API.
     *
     * The following inputs are necessary:
     * Grant type, Access Token URL, Client ID, Client Secret and Scope
     */

    public static Mono<String> getToken(String base_url, String client_id, String client_secret, String scope, String access_token_url) throws Exception {
        WebClient webClient = WebClient.create();

        return webClient.post()
                .uri(access_token_url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        .with("client_id", client_id)
                        .with("client_secret", client_secret)
                        .with("scope", base_url + scope))
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    JSONObject jsonResponse = new JSONObject(response);
                    return jsonResponse.getString("access_token");
                });
    }
}
