package de.ilume.dynamicsConnector.dto;

import io.camunda.connector.generator.java.annotation.TemplateProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Wrapper object for incoming requests to the connector
 *
 * @param authentication holds authorization values
 * @param target Sets the type of the changes your requests will execute (ex. "account" for table change requests)
 * @param operation The selected operation your request will execute
 * @param fields Specifies through a list of Strings (as a FEEL expression) which fields should be listed inside
 * the returned record of your request
 * @param accountId Specify which entry you want to address with your request by stating its account ID
 * @param requestBody Specify additional data you want to add to your request (as a FEEL expression)
 */
public record DynamicsConnectorRequest(
        @NotNull Authentication authentication,

        @NotEmpty @TemplateProperty(
                group = "operationGroup",
                type = TemplateProperty.PropertyType.Dropdown,
                choices = {
                        @TemplateProperty.DropdownPropertyChoice(value = "account", label = "account"),
                }
        )
        String target,

        @NotEmpty @TemplateProperty(
                condition = @TemplateProperty.PropertyCondition(property = "target", equals = "account"),
                group = "operationGroup",
                type = TemplateProperty.PropertyType.Dropdown,
                choices = {
                        @TemplateProperty.DropdownPropertyChoice(value = "getAll", label = "Get All Table Information"),
                        @TemplateProperty.DropdownPropertyChoice(value = "getEntry", label = "Get Table Entry"),
                        @TemplateProperty.DropdownPropertyChoice(value = "createEntry", label = "Create Table Entry"),
                        @TemplateProperty.DropdownPropertyChoice(value = "updateEntry", label = "Update Table Entry"),
                        @TemplateProperty.DropdownPropertyChoice(value = "deleteEntry", label = "Delete Table Entry"),
                }
        )
        String operation,

        @TemplateProperty(
                condition = @TemplateProperty.PropertyCondition(property = "operation", equals = "getEntry"),
                group = "operationGroup",
                label = "Fields",
                description = "Specify through a String list which fields should be listed inside the returned record of your request.")
        List<String> fields,

        @TemplateProperty(
                condition = @TemplateProperty.PropertyCondition(property = "operation", oneOf = {"getEntry", "updateEntry", "deleteEntry"}),
                group = "operationGroup",
                label = "Account ID")
        String accountId,

        @TemplateProperty(
                group = "operationGroup",
                label = "Request Body",
                description = "Enter a FEEL expression for the request body."
        )
        Map<String, Object> requestBody
) {}
