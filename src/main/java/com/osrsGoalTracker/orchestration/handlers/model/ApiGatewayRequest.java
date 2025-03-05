package com.osrsGoalTracker.orchestration.handlers.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing an API Gateway request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiGatewayRequest {
    /**
     * Path parameters from the API Gateway request.
     */
    private Map<String, String> pathParameters;

    /**
     * The request body as a JSON string.
     */
    private String body;
}