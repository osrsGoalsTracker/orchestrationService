package com.osrsGoalTracker.orchestration.handler.model.request;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing the request body for goal creation event production.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalCreationRequestEventProducerRequestBody {
    /**
     * The name of the skill or activity (e.g., "WOODCUTTING", "BOUNTY_HUNTER").
     */
    private String targetAttribute;

    /**
     * The type of goal (e.g., SKILL, ACTIVITY).
     */
    private String targetType;

    /**
     * The target value to achieve.
     */
    private long targetValue;

    /**
     * The current value towards the goal.
     */
    private long currentValue;

    /**
     * The deadline for this goal.
     */
    private Instant targetDate;

    /**
     * The notification channel type (e.g., DISCORD, EMAIL).
     */
    private String notificationChannelType;

    /**
     * The frequency of notifications (e.g., DAILY, WEEKLY, MONTHLY).
     */
    private String frequency;
}