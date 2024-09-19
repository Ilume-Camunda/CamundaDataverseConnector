package de.ilume.dynamicsConnector.dto;

import io.camunda.connector.generator.java.annotation.TemplateProperty;
import jakarta.validation.constraints.NotEmpty;


/**
 * Authentication Object representing all authorization values necessary to establish a connection
 *
 * @param base The Dynamics URL of your environment
 * @param client The App ID of the application used inside your Dataverse environment
 * @param secret The Client Secret for your created application
 * @param scope Scope of the Access Request
 * @param access The URL used to request the access token
 */
public record Authentication(
        @NotEmpty @TemplateProperty(
                group = "authenticationGroup",
                label = "Base URL",
                description = "The base URL of the access request. Usually: https://{Url of Dynamics 365 Web API}/.")
        String base,

        @NotEmpty @TemplateProperty(
                group = "authenticationGroup",
                label = "Client ID",
                description = "The app ID of your Dynamics Application.")
        String client,

        @NotEmpty @TemplateProperty(
                group = "authenticationGroup",
                label = "Client secret")
        String secret,

        @NotEmpty @TemplateProperty(
                group = "authenticationGroup",
                label = "Scope")
        String scope,

        @NotEmpty @TemplateProperty(
                group = "authenticationGroup",
                label = "Access token URL")
        String access) {
}
