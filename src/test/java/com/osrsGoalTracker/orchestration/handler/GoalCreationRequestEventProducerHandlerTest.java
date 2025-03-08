package com.osrsGoalTracker.orchestration.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.eventbridge.AmazonEventBridge;
import com.amazonaws.services.eventbridge.model.PutEventsRequest;
import com.amazonaws.services.eventbridge.model.PutEventsResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.osrsGoalTracker.orchestration.util.EnvUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Tests for the GoalCreationRequestEventProducerHandler.
 */
class GoalCreationRequestEventProducerHandlerTest {

    private GoalCreationRequestEventProducerHandler handler;

    @Mock
    private Context context;

    private AmazonEventBridge eventBridge;
    private EnvUtil envUtil;

    /**
     * Test module that provides mock dependencies.
     */
    static class TestModule extends AbstractModule {
        private final AmazonEventBridge mockEventBridge;
        private final EnvUtil mockEnvUtil;

        public TestModule(AmazonEventBridge mockEventBridge, EnvUtil mockEnvUtil) {
            this.mockEventBridge = mockEventBridge;
            this.mockEnvUtil = mockEnvUtil;
        }

        @Override
        protected void configure() {
            // No bindings needed
        }

        @Provides
        @Singleton
        public EnvUtil provideEnvUtil() {
            return mockEnvUtil;
        }

        @Provides
        @Singleton
        public AmazonEventBridge provideAmazonEventBridge() {
            return mockEventBridge;
        }
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create mocks
        eventBridge = mock(AmazonEventBridge.class);
        envUtil = mock(EnvUtil.class);

        // Configure mocks
        when(envUtil.getEnvVariable("GOAL_CREATION_REQUEST_EVENT_DETAIL_TYPE")).thenReturn("test-detail-type");
        when(envUtil.getEnvVariable("EVENT_BUS_NAME")).thenReturn("test-event-bus");
        when(eventBridge.putEvents(any(PutEventsRequest.class))).thenReturn(new PutEventsResult());

        // Create handler with test module
        handler = new GoalCreationRequestEventProducerHandler(
                Guice.createInjector(new TestModule(eventBridge, envUtil)),
                eventBridge,
                envUtil);
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

        // Verify EventBridge was called
        ArgumentCaptor<PutEventsRequest> requestCaptor = ArgumentCaptor.forClass(PutEventsRequest.class);
        verify(eventBridge).putEvents(requestCaptor.capture());

        // Verify the event bus name was set correctly
        PutEventsRequest capturedRequest = requestCaptor.getValue();
        assertEquals(1, capturedRequest.getEntries().size());
        assertEquals("test-event-bus", capturedRequest.getEntries().get(0).getEventBusName());

        // Verify EnvUtil was called for both environment variables
        verify(envUtil).getEnvVariable("GOAL_CREATION_REQUEST_EVENT_DETAIL_TYPE");
        verify(envUtil).getEnvVariable("EVENT_BUS_NAME");
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

    @Test
    void testHandleRequest_whenEventBridgeFails_returnsErrorResponse() {
        // Given
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("userId", "user123");
        pathParameters.put("name", "characterName");

        String body = "{\"targetAttribute\":\"WOODCUTTING\",\"targetType\":\"SKILL\",\"targetValue\":99,\"currentValue\":1,\"targetDate\":\"2024-12-31T23:59:59Z\",\"notificationChannelType\":\"EMAIL\",\"frequency\":\"DAILY\"}";

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withPathParameters(pathParameters)
                .withBody(body);

        // Configure EventBridge to throw an exception
        when(eventBridge.putEvents(any(PutEventsRequest.class))).thenThrow(new RuntimeException("EventBridge error"));

        // When
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Then
        assertNotNull(response);
        assertEquals(500, response.getStatusCode());
    }
}