package com.osrsGoalTracker.orchestration.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.osrsGoalTracker.orchestration.di.GoalCreationRequestEventProducerModule;
import lombok.extern.log4j.Log4j2;

/**
 * Lambda handler for producing GoalCreationRequestEvents.
 * This handler is responsible for receiving goal creation requests and
 * publishing them as events.
 */
@Log4j2
public class GoalCreationRequestEventProducerLambda implements RequestHandler<Object, String> {

    private final Injector injector;

    /**
     * Constructor that initializes the Guice injector.
     */
    public GoalCreationRequestEventProducerLambda() {
        this.injector = Guice.createInjector(new GoalCreationRequestEventProducerModule());
        log.info("GoalCreationRequestEventProducer initialized");
    }

    /**
     * Handles the Lambda request.
     *
     * @param input   The Lambda function input
     * @param context The Lambda execution context
     * @return A simple response message
     */
    @Override
    public String handleRequest(Object input, Context context) {
        log.info("Hello World from GoalCreationRequestEventProducer");
        return "Hello World from GoalCreationRequestEventProducer";
    }
}