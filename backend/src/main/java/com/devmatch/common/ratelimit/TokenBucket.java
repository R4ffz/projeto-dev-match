package com.devmatch.common.ratelimit;

import java.util.function.LongSupplier;

class TokenBucket {

    private final long capacity;
    private final long refillIntervalMillis;
    private final LongSupplier clock;
    private double tokens;
    private long lastRefillMillis;

    TokenBucket(long capacity, long refillIntervalMillis) {
        this(capacity, refillIntervalMillis, System::currentTimeMillis);
    }

    TokenBucket(long capacity, long refillIntervalMillis, LongSupplier clock) {
        this.capacity = capacity;
        this.refillIntervalMillis = refillIntervalMillis;
        this.clock = clock;
        this.tokens = capacity;
        this.lastRefillMillis = clock.getAsLong();
    }

    synchronized boolean tryConsume() {
        refill();
        if (tokens >= 1.0) {
            tokens -= 1.0;
            return true;
        }
        return false;
    }

    synchronized long secondsUntilNextToken() {
        refill();
        if (tokens >= 1.0) {
            return 0;
        }
        double tokensNeeded = 1.0 - tokens;
        double millisPerToken = (double) refillIntervalMillis / capacity;
        return (long) Math.ceil(tokensNeeded * millisPerToken / 1000.0);
    }

    private void refill() {
        long now = clock.getAsLong();
        long elapsed = now - lastRefillMillis;
        if (elapsed <= 0) {
            return;
        }
        double tokensToAdd = (double) elapsed * capacity / refillIntervalMillis;
        tokens = Math.min(capacity, tokens + tokensToAdd);
        lastRefillMillis = now;
    }
}
