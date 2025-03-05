package com.osrsGoalTracker.orchestration.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the GoalCreationRequestEventProducer lambda handler.
 */
class GoalCreationRequestEventProducerTest {

    private GoalCreationRequestEventProducerLambda handler;

    @Mock
    private Context context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new GoalCreationRequestEventProducerLambda();
    }

    @Test
    void testHandleRequest_returnsHelloWorld() {
        // Given
        Object input = new Object();

        // When
        String result = handler.handleRequest(input, context);

        // Then
        assertEquals("Hello World from GoalCreationRequestEventProducer", result);
    }
}