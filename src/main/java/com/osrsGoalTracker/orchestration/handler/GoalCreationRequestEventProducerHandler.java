package com.osrsGoalTracker.orchestration.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.osrsGoalTracker.orchestration.di.GoalCreationRequestEventProducerModule;
import com.osrsGoalTracker.orchestration.events.GoalCreationRequestEvent;
import com.osrsGoalTracker.orchestration.handler.model.request.GoalCreationRequestEventProducerRequestBody;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Lambda handler for producing GoalCreationRequestEvents.
 * This handler is responsible for receiving goal creation requests via API
 * Gateway and publishing them as events.
 */
@Log4j2
public class GoalCreationRequestEventProducerHandler
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final Injector injector;

    /**
     * Default constructor for AWS Lambda.
     */
    public GoalCreationRequestEventProducerHandler() {
        this.injector = Guice.createInjector(new GoalCreationRequestEventProducerModule());
        log.info("GoalCreationRequestEventProducerHandler initialized");
    }

    /**
     * Constructor with injector for testing.
     *
     * @param injector The Guice injector
     */
    public GoalCreationRequestEventProducerHandler(Injector injector) {
        this.injector = injector;
        log.info("GoalCreationRequestEventProducerHandler initialized with injector");
    }

    /**
     * Handles the Lambda request from API Gateway.
     *
     * @param request The API Gateway request
     * @param context The Lambda execution context
     * @return An API Gateway response
     */
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        log.info("Received request: {}", request);

        try {
            // Step 1: Parse and validate input
            GoalCreationRequestEvent event = parseAndValidateInput(request);

            // Step 2: Execute business logic
            executeBusinessLogic(event);

            // Step 3: Create and return response
            return createSuccessResponse("Goal creation request received successfully");
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage(), e);
            return createErrorResponse(HTTP_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error processing request: {}", e.getMessage(), e);
            return createErrorResponse(HTTP_INTERNAL_ERROR, "Internal server error: " + e.getMessage());
        }
    }

    /**
     * Parses and validates the input request.
     *
     * @param request The API Gateway request
     * @return A GoalCreationRequestEvent
     * @throws IllegalArgumentException if the input is invalid
     */
    private GoalCreationRequestEvent parseAndValidateInput(APIGatewayProxyRequestEvent request)
            throws IllegalArgumentException {
        validateRequest(request);

        Map<String, String> pathParams = request.getPathParameters();
        validatePathParameters(pathParams);

        String userId = validateAndGetUserId(pathParams);
        String characterName = validateAndGetCharacterName(pathParams);

        GoalCreationRequestEventProducerRequestBody requestBody = parseRequestBody(request.getBody());

        return buildGoalCreationRequestEvent(userId, characterName, requestBody);
    }

    /**
     * Validates that the request is not null.
     *
     * @param request The API Gateway request
     * @throws IllegalArgumentException if the request is null
     */
    private void validateRequest(APIGatewayProxyRequestEvent request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
    }

    /**
     * Validates that the path parameters are present.
     *
     * @param pathParams The path parameters
     * @throws IllegalArgumentException if the path parameters are missing
     */
    private void validatePathParameters(Map<String, String> pathParams) {
        if (pathParams == null) {
            throw new IllegalArgumentException("Missing path parameters");
        }
    }

    /**
     * Validates and retrieves the userId from path parameters.
     *
     * @param pathParams The path parameters
     * @return The validated userId
     * @throws IllegalArgumentException if the userId is missing or empty
     */
    private String validateAndGetUserId(Map<String, String> pathParams) {
        String userId = pathParams.get("userId");
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing userId in path parameters");
        }
        return userId.trim();
    }

    /**
     * Validates and retrieves the characterName from path parameters.
     *
     * @param pathParams The path parameters
     * @return The validated characterName
     * @throws IllegalArgumentException if the characterName is missing or empty
     */
    private String validateAndGetCharacterName(Map<String, String> pathParams) {
        String characterName = pathParams.get("name");
        if (characterName == null || characterName.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing characterName in path parameters");
        }
        return characterName.trim();
    }

    /**
     * Parses the request body into a GoalCreationRequestEventProducerRequestBody
     * object.
     *
     * @param body The request body as a string
     * @return The parsed GoalCreationRequestEventProducerRequestBody
     * @throws IllegalArgumentException if the body is missing or invalid
     */
    private GoalCreationRequestEventProducerRequestBody parseRequestBody(String body) {
        if (body == null || body.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing request body");
        }

        try {
            return OBJECT_MAPPER.readValue(body, GoalCreationRequestEventProducerRequestBody.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse request body: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Invalid request body: " + e.getMessage());
        }
    }

    /**
     * Builds a GoalCreationRequestEvent from the validated inputs.
     *
     * @param userId        The userId
     * @param characterName The characterName
     * @param requestBody   The request body
     * @return A GoalCreationRequestEvent
     */
    private GoalCreationRequestEvent buildGoalCreationRequestEvent(
            String userId,
            String characterName,
            GoalCreationRequestEventProducerRequestBody requestBody) {
        return GoalCreationRequestEvent.builder()
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
    }

    /**
     * Executes the business logic for the request.
     *
     * @param event The GoalCreationRequestEvent
     */
    private void executeBusinessLogic(GoalCreationRequestEvent event) {
        // Log the event for debugging
        log.info("Processing GoalCreationRequestEvent: {}", event);

        // In a real implementation, this would publish the event to an event bus or
        // queue
        // For now, we just log it
    }

    /**
     * Creates a success response with the given message.
     *
     * @param message The success message
     * @return An API Gateway response
     */
    private APIGatewayProxyResponseEvent createSuccessResponse(String message) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String responseBody;
        try {
            responseBody = OBJECT_MAPPER.writeValueAsString(Map.of("message", message));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize response body: {}", e.getMessage(), e);
            responseBody = "{\"message\":\"" + message + "\"}";
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(HTTP_OK)
                .withHeaders(headers)
                .withBody(responseBody)
                .withIsBase64Encoded(false);
    }

    /**
     * Creates an error response with the given status code and message.
     *
     * @param statusCode The HTTP status code
     * @param message    The error message
     * @return An API Gateway response
     */
    private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String message) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String responseBody;
        try {
            responseBody = OBJECT_MAPPER.writeValueAsString(Map.of("error", message));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize error response body: {}", e.getMessage(), e);
            responseBody = "{\"error\":\"" + message + "\"}";
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withHeaders(headers)
                .withBody(responseBody)
                .withIsBase64Encoded(false);
    }
}