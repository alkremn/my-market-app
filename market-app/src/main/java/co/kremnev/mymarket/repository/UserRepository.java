package co.kremnev.mymarket.repository;

import co.kremnev.mymarket.model.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<User, Long> {
    Mono<User> findByUsername(String username);
    Mono<Boolean> existsByUsername(String username);
}
