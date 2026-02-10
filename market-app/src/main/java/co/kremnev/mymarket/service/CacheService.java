package co.kremnev.mymarket.service;

import reactor.core.publisher.Mono;

public interface CacheService {
    Mono<Object> get(String key);
    Mono<Boolean> set(String key, Object value);
    Mono<Long> delete(String key);
}
