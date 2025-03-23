package com.osrsGoalTracker.orchestration.events;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request model for when a goal progress is updated.
 */
@Data
@NoArgsConstructor
public class GoalProgressUpdateEvent {
  private String userId;
  private String characterName;
  private String goalId;
  private long progressValue;
}