package com.devmatch.common.ratelimit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenBucketTest {

    @Test
    @DisplayName("comeca cheio: consome ate a capacidade e entao falha")
    void capacidadeInicial() {
        AtomicLong clock = new AtomicLong(0L);
        TokenBucket bucket = new TokenBucket(5L, 60_000L, clock::get);

        for (int i = 0; i < 5; i++) {
            assertTrue(bucket.tryConsume(), "consume #" + (i + 1) + " deve passar");
        }
        assertFalse(bucket.tryConsume(), "consume #6 deve falhar");
    }

    @Test
    @DisplayName("refill proportional: apos 1/5 do intervalo, 1 token volta")
    void refillProporcional() {
        AtomicLong clock = new AtomicLong(0L);
        TokenBucket bucket = new TokenBucket(5L, 60_000L, clock::get);

        for (int i = 0; i < 5; i++) bucket.tryConsume();
        assertFalse(bucket.tryConsume(), "esgotou no t=0");

        clock.set(12_000L);
        assertTrue(bucket.tryConsume(), "apos 12s (1/5 de 60s), 1 token deve ter sido recuperado");
        assertFalse(bucket.tryConsume(), "mas so 1 token disponivel");
    }

    @Test
    @DisplayName("refill nao excede capacidade mesmo apos longo intervalo")
    void refillNaoUltrapassaCapacidade() {
        AtomicLong clock = new AtomicLong(0L);
        TokenBucket bucket = new TokenBucket(5L, 60_000L, clock::get);

        for (int i = 0; i < 5; i++) bucket.tryConsume();
        clock.set(1_000_000L);

        for (int i = 0; i < 5; i++) {
            assertTrue(bucket.tryConsume(), "consume #" + (i + 1) + " apos longo intervalo deve passar");
        }
        assertFalse(bucket.tryConsume(), "capacidade limitada mesmo apos longo intervalo");
    }

    @Test
    @DisplayName("secondsUntilNextToken: zero quando ha tokens, positivo quando vazio")
    void secondsUntilNextToken() {
        AtomicLong clock = new AtomicLong(0L);
        TokenBucket bucket = new TokenBucket(5L, 60_000L, clock::get);

        assertEquals(0L, bucket.secondsUntilNextToken(), "cheio: sem espera");

        for (int i = 0; i < 5; i++) bucket.tryConsume();
        long wait = bucket.secondsUntilNextToken();
        assertTrue(wait > 0L && wait <= 12L,
            "vazio: aprox 12s ate proximo token (60s/5 tokens), got " + wait);
    }
}
