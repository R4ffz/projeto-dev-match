package com.devmatch.common.ratelimit;

import com.devmatch.common.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    static final int LOGIN_CAPACITY = 5;
    static final long LOGIN_REFILL_MILLIS = 60_000L;
    static final int REGISTER_CAPACITY = 3;
    static final long REGISTER_REFILL_MILLIS = 3_600_000L;

    private final ObjectMapper objectMapper;

    private final Map<String, TokenBucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<String, TokenBucket> registerBuckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

        TokenBucket bucket = pickBucket(request);
        if (bucket == null) {
            chain.doFilter(request, response);
            return;
        }

        if (bucket.tryConsume()) {
            chain.doFilter(request, response);
            return;
        }

        long retryAfter = bucket.secondsUntilNextToken();
        writeTooManyRequests(request, response, retryAfter);
    }

    private TokenBucket pickBucket(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return null;
        }
        String uri = request.getRequestURI();
        String clientIp = clientIp(request);
        if ("/api/auth/login".equals(uri)) {
            return loginBuckets.computeIfAbsent(clientIp, ip -> new TokenBucket(LOGIN_CAPACITY, LOGIN_REFILL_MILLIS));
        }
        if ("/api/auth/register".equals(uri)) {
            return registerBuckets.computeIfAbsent(clientIp, ip -> new TokenBucket(REGISTER_CAPACITY, REGISTER_REFILL_MILLIS));
        }
        return null;
    }

    private String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void writeTooManyRequests(HttpServletRequest request, HttpServletResponse response, long retryAfter) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfter));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiError body = ApiError.of(
            HttpStatus.TOO_MANY_REQUESTS.value(),
            "Too Many Requests",
            "Rate limit excedido. Tente novamente em " + retryAfter + "s.",
            request.getRequestURI()
        );
        objectMapper.writeValue(response.getWriter(), body);
    }
}
