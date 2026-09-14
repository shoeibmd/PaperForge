package com.paperforge.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paperforge.dto.ErrorResponseDto;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter implements Filter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(120)
                .refillGreedy(120, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = httpRequest.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(clientIp, k -> createNewBucket());

        io.github.bucket4j.ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        httpResponse.setHeader("X-RateLimit-Limit", "120");
        httpResponse.setHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));
        httpResponse.setHeader("X-RateLimit-Reset", String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000L));

        if (probe.isConsumed()) {
            chain.doFilter(request, response);
        } else {
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            ErrorResponseDto errorDto = new ErrorResponseDto("Too Many Requests", "Rate limit exceeded. Please try again later.");
            httpResponse.getWriter().write(objectMapper.writeValueAsString(errorDto));
        }
    }
}
