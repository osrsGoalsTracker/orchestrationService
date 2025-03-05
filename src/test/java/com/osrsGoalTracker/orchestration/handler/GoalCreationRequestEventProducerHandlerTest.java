package com.osrsGoalTracker.orchestration.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for the GoalCreationRequestEventProducerHandler.
 */
class GoalCreationRequestEventProducerHandlerTest {

    private GoalCreationRequestEventProducerHandler handler;

    @Mock
    private Context context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new GoalCreationRequestEventProducerHandler();
    }

    @Test
    void testHandleRequest_withValidInput_returnsSuccessResponse() {
        // Given
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("userId", "user123");
        pathParameters.put("name", "characterName");

        String body = "{\"targetAttribute\":\"WOODCUTTING\",\"targetType\":\"SKILL\",\"targetValue\":99,\"currentValue\":1,\"targetDate\":\"2024-12-31T23:59:59Z\",\"notificationChannelType\":\"EMAIL\",\"frequency\":\"DAILY\"}";

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withPathParameters(pathParameters)
                .withBody(body);

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
    }

    @Test
    void testHandleRequest_withMissingPathParameters_returnsErrorResponse() {
        // Given
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withBody(
                        "{\"targetAttribute\":\"WOODCUTTING\",\"targetType\":\"SKILL\",\"targetValue\":99,\"currentValue\":1,\"targetDate\":\"2024-12-31T23:59:59Z\",\"notificationChannelType\":\"EMAIL\",\"frequency\":\"DAILY\"}");

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void testHandleRequest_withMissingUserId_returnsErrorResponse() {
        // Given
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("name", "characterName");

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withPathParameters(pathParameters)
                .withBody(
                        "{\"targetAttribute\":\"WOODCUTTING\",\"targetType\":\"SKILL\",\"targetValue\":99,\"currentValue\":1,\"targetDate\":\"2024-12-31T23:59:59Z\",\"notificationChannelType\":\"EMAIL\",\"frequency\":\"DAILY\"}");

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void testHandleRequest_withMissingCharacterName_returnsErrorResponse() {
        // Given
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("userId", "user123");

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withPathParameters(pathParameters)
                .withBody(
                        "{\"targetAttribute\":\"WOODCUTTING\",\"targetType\":\"SKILL\",\"targetValue\":99,\"currentValue\":1,\"targetDate\":\"2024-12-31T23:59:59Z\",\"notificationChannelType\":\"EMAIL\",\"frequency\":\"DAILY\"}");

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

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

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withPathParameters(pathParameters);

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

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

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withPathParameters(pathParameters)
                .withBody("invalid json");

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
    }
}