package com.ded.necronmod.radio.server;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Ограничитель частоты запросов (token bucket) на игрока, по тикам сервера. Без зависимостей от Minecraft.
 * Экран шлёт настройки не чаще 5 раз в секунду; лимит с запасом (всплеск 10, затем 8 в секунду) отсекает
 * только намеренный спам: каждый принятый пакет рассылается всем клиентам вокруг.
 * Вызывается из главного потока сервера.
 */
public final class RateLimiter {
    private static final class Bucket {
        double tokens;
        long lastTick;
        Bucket(double tokens, long lastTick) { this.tokens = tokens; this.lastTick = lastTick; }
    }

    private final double capacity;
    private final double refillPerTick;
    private final Map<UUID, Bucket> buckets = new HashMap<>();

    public RateLimiter(double capacity, double refillPerSecond) {
        this.capacity = capacity;
        this.refillPerTick = refillPerSecond / 20.0;
    }

    /** @return true, если запрос разрешён (и списывает один токен) */
    public boolean allow(UUID id, long tick) {
        Bucket b = buckets.computeIfAbsent(id, k -> new Bucket(capacity, tick));
        long elapsed = Math.max(0, tick - b.lastTick);
        b.tokens = Math.min(capacity, b.tokens + elapsed * refillPerTick);
        b.lastTick = tick;
        if (b.tokens >= 1.0) {
            b.tokens -= 1.0;
            return true;
        }
        return false;
    }

    public void forget(UUID id) { buckets.remove(id); }
}
