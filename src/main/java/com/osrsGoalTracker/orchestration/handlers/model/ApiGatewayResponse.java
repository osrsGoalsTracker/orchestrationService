package com.osrsGoalTracker.orchestration.handlers.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing an API Gateway response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiGatewayResponse {
    /**
     * The HTTP status code.
     */
    private int statusCode;

    /**
     * Response headers.
     */
    private Map<String, String> headers;

    /**
     * The response body as a JSON string.
     */
    private String body;

    /**
     * Whether the response is base64 encoded.
     */
    private boolean isBase64Encoded;
}