package com.osrsGoalTracker.orchestration.handler.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing the response for goal creation event production.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalCreationRequestEventProducerResponse {
    /**
     * The status message of the goal creation request event production.
     */
    private String message;
}