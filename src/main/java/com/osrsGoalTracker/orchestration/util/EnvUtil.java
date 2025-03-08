package com.osrsGoalTracker.orchestration.util;

import java.util.Map;

import lombok.extern.log4j.Log4j2;

/**
 * Utility class for accessing environment variables.
 * Provides methods to retrieve environment variables from the system.
 */
@Log4j2
public class EnvUtil {

    /**
     * Default constructor.
     * Logs a message when the utility is initialized.
     */
    public EnvUtil() {
        log.info("EnvUtil initialized");
    }

    /**
     * Retrieves the value of an environment variable.
     * 
     * @param key The name of the environment variable to retrieve
     * @return The value of the environment variable, or null if not found
     */
    public String getEnvVariable(String key) {
        Map<String, String> env = System.getenv();
        return env.get(key);
    }
}
