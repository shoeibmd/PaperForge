package com.paperforge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceLimitsConfig {

    @Value("${paperforge.default-user-quota-bytes:524288000}")
    private long defaultUserQuotaBytes;

    @Value("${paperforge.max-concurrent-jobs:10}")
    private int maxConcurrentJobs;

    @Value("${paperforge.job-timeout-seconds:120}")
    private long jobTimeoutSeconds;

    public long getDefaultUserQuotaBytes() {
        return defaultUserQuotaBytes;
    }

    public int getMaxConcurrentJobs() {
        return maxConcurrentJobs;
    }

    public long getJobTimeoutSeconds() {
        return jobTimeoutSeconds;
    }
}
