package com.osrsGoalTracker.orchestration.util;

import java.util.Map;

public class EnvUtil {
    public EnvUtil() {
        System.out.println("EnvUtil initialized");
    }

    public String getEnvVariable(String key) {
        Map<String, String> env = System.getenv();
        return env.get(key);
    }
}
