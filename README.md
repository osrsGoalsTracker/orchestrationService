# OSRS Goal Tracker Orchestration Service

This repository contains the event models and orchestration service for the OSRS Goal Tracker system.

## Using Event Models in Your Project

To use the event models in your project, add the following dependency:

### Gradle
```groovy
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation "com.github.osrsGoalsTracker:orchestrationService:1.1.0"  // Use latest version
}
```

### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.osrsGoalsTracker</groupId>
        <artifactId>orchestrationService</artifactId>
        <version>1.1.0</version>  <!-- Use latest version -->
    </dependency>
</dependencies>
```

## Available Models

### GoalCreationRequestEvent

This event is used to request the creation of a new goal. Example usage:

```java
import com.osrsGoalTracker.orchestration.events.GoalCreationRequestEvent;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

GoalCreationRequestEvent event = GoalCreationRequestEvent.builder()
    .userId("user123")
    .characterName("Zezima")
    .targetAttribute("WOODCUTTING")
    .targetType("SKILL")
    .targetValue(99)
    .currentValue(1)
    .targetDate(Instant.now().plus(30, ChronoUnit.DAYS))
    .notificationChannelType("DISCORD")
    .frequency("DAILY")
    .build();
```

## Versioning

We use semantic versioning (MAJOR.MINOR.PATCH). Check the [releases page](https://github.com/osrsGoalsTracker/orchestrationService/releases) for the latest version.
