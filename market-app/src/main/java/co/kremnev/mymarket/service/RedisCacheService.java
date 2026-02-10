package co.kremnev.mymarket.service;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class RedisCacheService implements CacheService {
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);
    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    public RedisCacheService(ReactiveRedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Object> get(String key) {
        return  redisTemplate.opsForValue().get(key);
    }

    @Override
    public Mono<Boolean> set(String key, Object value) {
        return redisTemplate.opsForValue().set(key, value, CACHE_TTL);
    }

    @Override
    public Mono<Long> delete(String key) {
        return redisTemplate.delete(key);
    }
}
