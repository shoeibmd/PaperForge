package com.paperforge.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Timer pdfProcessingTimer(MeterRegistry registry) {
        return Timer.builder("paperforge_pdf_process_duration_seconds")
                .description("Duration of PaperForge PDF processing operations in seconds")
                .register(registry);
    }

    @Bean
    public Counter securityEventCounter(MeterRegistry registry) {
        return Counter.builder("paperforge_security_events_total")
                .description("Total count of PaperForge security audit events")
                .register(registry);
    }

    @Bean
    public Counter failedAuthCounter(MeterRegistry registry) {
        return Counter.builder("paperforge_failed_auth_total")
                .description("Total count of failed authentication attempts")
                .register(registry);
    }
}
