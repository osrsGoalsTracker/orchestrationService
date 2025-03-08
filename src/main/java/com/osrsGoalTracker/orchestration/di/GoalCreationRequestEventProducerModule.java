package com.osrsGoalTracker.orchestration.di;

import com.amazonaws.services.eventbridge.AmazonEventBridge;
import com.amazonaws.services.eventbridge.AmazonEventBridgeClientBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.osrsGoalTracker.orchestration.util.EnvUtil;

/**
 * Guice module for the GoalCreationRequestEventProducerHandler.
 * This module binds all dependencies required by the handler.
 */
public class GoalCreationRequestEventProducerModule extends AbstractModule {

    @Override
    protected void configure() {
        // Bind dependencies here when needed
    }

    /**
     * Provides an instance of EnvUtil.
     * 
     * @return A singleton instance of EnvUtil
     */
    @Provides
    @Singleton
    public EnvUtil provideEnvUtil() {
        return new EnvUtil();
    }

    /**
     * Provides an instance of AmazonEventBridge.
     * 
     * @return A singleton instance of AmazonEventBridge
     */
    @Provides
    @Singleton
    public AmazonEventBridge provideAmazonEventBridge() {
        return AmazonEventBridgeClientBuilder.defaultClient();
    }
}