package com.vertra.infrastructure.filter;

import com.vertra.domain.exception.RateLimitExceededException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Value("${rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${rate-limit.default-limit:100}")
    private int defaultLimit;

    @Value("${rate-limit.window-seconds:3600}")
    private long windowSeconds;

    private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (!rateLimitEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientId = getClientIdentifier(request);
        RateLimitInfo rateLimitInfo = rateLimitMap.computeIfAbsent(
                clientId,
                k -> new RateLimitInfo(defaultLimit, windowSeconds)
        );

        if (rateLimitInfo.isExpired()) {
            rateLimitInfo.reset();
        }

        response.setHeader("X-RateLimit-Limit", String.valueOf(defaultLimit));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(rateLimitInfo.getRemaining()));
        response.setHeader("X-RateLimit-Reset", String.valueOf(rateLimitInfo.getResetAt().getEpochSecond()));

        if (!rateLimitInfo.canProceed()) {
            log.warn("Rate limit exceeded for client: {} - Limit: {}, Reset at: {}",
                    clientId, defaultLimit, rateLimitInfo.getResetAt());

            throw RateLimitExceededException.forIp(
                    defaultLimit,
                    rateLimitInfo.getResetAt()
            );
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/actuator/health") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs");
    }

    private String getClientIdentifier(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    /**
     * Internal class to track rate limit information per client
     */
    private static class RateLimitInfo {
        private final int limit;
        private final long windowSeconds;
        private final AtomicInteger count;
        private volatile Instant windowStart;
        private volatile Instant resetAt;

        RateLimitInfo(int limit, long windowSeconds) {
            this.limit = limit;
            this.windowSeconds = windowSeconds;
            this.count = new AtomicInteger(0);
            this.windowStart = Instant.now();
            this.resetAt = windowStart.plusSeconds(windowSeconds);
        }

        boolean canProceed() {
            return count.incrementAndGet() <= limit;
        }

        int getRemaining() {
            return Math.max(0, limit - count.get());
        }

        boolean isExpired() {
            return Instant.now().isAfter(resetAt);
        }

        Instant getResetAt() {
            return resetAt;
        }

        void reset() {
            count.set(0);
            windowStart = Instant.now();
            resetAt = windowStart.plusSeconds(windowSeconds);
        }
    }
}
