package com.osrsGoalTracker.orchestration.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.osrsGoalTracker.orchestration.events.GoalCreationRequestEvent;
import com.osrsGoalTracker.orchestration.di.GoalCreationRequestModule;
import com.osrsGoalTracker.orchestration.handlers.model.ApiGatewayRequest;
import com.osrsGoalTracker.orchestration.handlers.model.ApiGatewayResponse;
import com.osrsGoalTracker.orchestration.handlers.model.GoalCreationRequestBody;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

/**
 * Lambda handler for producing GoalCreationRequestEvents.
 * This handler is responsible for receiving goal creation requests via API
 * Gateway and publishing them as events.
 */
@Log4j2
public class GoalCreationRequestHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayResponse> {

    private static final int HTTP_OK = 200;
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_INTERNAL_SERVER_ERROR = 500;

    private final Injector injector;
    private final ObjectMapper objectMapper;

    /**
     * Constructor that initializes the Guice injector and ObjectMapper.
     */
    public GoalCreationRequestHandler() {
        this.injector = Guice.createInjector(new GoalCreationRequestModule());
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        log.info("GoalCreationRequestHandler initialized");
    }

    /**
     * Handles the Lambda request from API Gateway.
     *
     * @param request The API Gateway request
     * @param context The Lambda execution context
     * @return An API Gateway response
     */
    @Override
    public ApiGatewayResponse handleRequest(ApiGatewayRequest request, Context context) {
        try {
            log.info("Received request: {}", request);

            // Extract path parameters
            Map<String, String> pathParams = request.getPathParameters();
            if (pathParams == null) {
                return createErrorResponse(HTTP_BAD_REQUEST, "Missing path parameters");
            }

            String userId = pathParams.get("userId");
            String characterName = pathParams.get("name");

            if (userId == null || userId.isEmpty()) {
                return createErrorResponse(HTTP_BAD_REQUEST, "Missing userId in path parameters");
            }

            if (characterName == null || characterName.isEmpty()) {
                return createErrorResponse(HTTP_BAD_REQUEST, "Missing characterName in path parameters");
            }

            // Parse request body
            String body = request.getBody();
            if (body == null || body.isEmpty()) {
                return createErrorResponse(HTTP_BAD_REQUEST, "Missing request body");
            }

            GoalCreationRequestBody requestBody;
            try {
                requestBody = objectMapper.readValue(body, GoalCreationRequestBody.class);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse request body: {}", e.getMessage(), e);
                return createErrorResponse(HTTP_BAD_REQUEST, "Invalid request body: " + e.getMessage());
            }

            // Create GoalCreationRequestEvent
            GoalCreationRequestEvent event = GoalCreationRequestEvent.builder()
                    .userId(userId)
                    .characterName(characterName)
                    .targetAttribute(requestBody.getTargetAttribute())
                    .targetType(requestBody.getTargetType())
                    .targetValue(requestBody.getTargetValue())
                    .currentValue(requestBody.getCurrentValue())
                    .targetDate(requestBody.getTargetDate())
                    .notificationChannelType(requestBody.getNotificationChannelType())
                    .frequency(requestBody.getFrequency())
                    .build();

            // Log the event for debugging
            log.info("Created GoalCreationRequestEvent: {}", event);

            // Publish the event to an event bus or queue (to be implemented)

            // Return successful response
            return createSuccessResponse("Goal creation request received successfully");
        } catch (Exception e) {
            log.error("Unexpected error processing request: {}", e.getMessage(), e);
            return createErrorResponse(HTTP_INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
        }
    }

    /**
     * Creates a success response with the given message.
     *
     * @param message The success message
     * @return An API Gateway response
     */
    private ApiGatewayResponse createSuccessResponse(String message) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String responseBody;
        try {
            responseBody = objectMapper.writeValueAsString(Map.of("message", message));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize response body: {}", e.getMessage(), e);
            responseBody = "{\"message\":\"" + message + "\"}";
        }

        return ApiGatewayResponse.builder()
                .statusCode(HTTP_OK)
                .headers(headers)
                .body(responseBody)
                .isBase64Encoded(false)
                .build();
    }

    /**
     * Creates an error response with the given status code and message.
     *
     * @param statusCode The HTTP status code
     * @param message    The error message
     * @return An API Gateway response
     */
    private ApiGatewayResponse createErrorResponse(int statusCode, String message) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String responseBody;
        try {
            responseBody = objectMapper.writeValueAsString(Map.of("error", message));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize error response body: {}", e.getMessage(), e);
            responseBody = "{\"error\":\"" + message + "\"}";
        }

        return ApiGatewayResponse.builder()
                .statusCode(statusCode)
                .headers(headers)
                .body(responseBody)
                .isBase64Encoded(false)
                .build();
    }
}