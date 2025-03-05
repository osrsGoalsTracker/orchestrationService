package com.osrsGoalTracker.orchestration.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.osrsGoalTracker.orchestration.handlers.model.ApiGatewayRequest;
import com.osrsGoalTracker.orchestration.handlers.model.ApiGatewayResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for the GoalCreationRequestHandler.
 */
class GoalCreationRequestHandlerTest {

    private GoalCreationRequestHandler handler;

    @Mock
    private Context context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new GoalCreationRequestHandler();
    }

    @Test
    void testHandleRequest_withValidInput_returnsSuccessResponse() {
        // Given
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("userId", "user123");
        pathParameters.put("name", "characterName");

        String body = "{\"targetAttribute\":\"WOODCUTTING\",\"targetType\":\"SKILL\",\"targetValue\":99,\"currentValue\":1,\"targetDate\":\"2024-12-31T23:59:59Z\",\"notificationChannelType\":\"EMAIL\",\"frequency\":\"DAILY\"}";

        ApiGatewayRequest request = ApiGatewayRequest.builder()
                .pathParameters(pathParameters)
                .body(body)
                .build();

        // When
        ApiGatewayResponse response = handler.handleRequest(request, context);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
    }

    @Test
    void testHandleRequest_withMissingPathParameters_returnsErrorResponse() {
        // Given
        ApiGatewayRequest request = ApiGatewayRequest.builder()
                .body("{\"targetAttribute\":\"WOODCUTTING\",\"targetType\":\"SKILL\",\"targetValue\":99,\"currentValue\":1,\"targetDate\":\"2024-12-31T23:59:59Z\",\"notificationChannelType\":\"EMAIL\",\"frequency\":\"DAILY\"}")
                .build();

        // When
        ApiGatewayResponse response = handler.handleRequest(request, context);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void testHandleRequest_withMissingUserId_returnsErrorResponse() {
        // Given
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("name", "characterName");

        ApiGatewayRequest request = ApiGatewayRequest.builder()
                .pathParameters(pathParameters)
                .body("{\"targetAttribute\":\"WOODCUTTING\",\"targetType\":\"SKILL\",\"targetValue\":99,\"currentValue\":1,\"targetDate\":\"2024-12-31T23:59:59Z\",\"notificationChannelType\":\"EMAIL\",\"frequency\":\"DAILY\"}")
                .build();

        // When
        ApiGatewayResponse response = handler.handleRequest(request, context);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void testHandleRequest_withMissingCharacterName_returnsErrorResponse() {
        // Given
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("userId", "user123");

        ApiGatewayRequest request = ApiGatewayRequest.builder()
                .pathParameters(pathParameters)
                .body("{\"targetAttribute\":\"WOODCUTTING\",\"targetType\":\"SKILL\",\"targetValue\":99,\"currentValue\":1,\"targetDate\":\"2024-12-31T23:59:59Z\",\"notificationChannelType\":\"EMAIL\",\"frequency\":\"DAILY\"}")
                .build();

        // When
        ApiGatewayResponse response = handler.handleRequest(request, context);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void testHandleRequest_withMissingBody_returnsErrorResponse() {
        // Given
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("userId", "user123");
        pathParameters.put("name", "characterName");

        ApiGatewayRequest request = ApiGatewayRequest.builder()
                .pathParameters(pathParameters)
                .build();

        // When
        ApiGatewayResponse response = handler.handleRequest(request, context);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void testHandleRequest_withInvalidBody_returnsErrorResponse() {
        // Given
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("userId", "user123");
        pathParameters.put("name", "characterName");

        ApiGatewayRequest request = ApiGatewayRequest.builder()
                .pathParameters(pathParameters)
                .body("invalid json")
                .build();

        // When
        ApiGatewayResponse response = handler.handleRequest(request, context);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
    }
}