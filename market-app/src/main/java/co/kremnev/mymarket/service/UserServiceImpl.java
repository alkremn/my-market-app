package co.kremnev.mymarket.service;

import co.kremnev.mymarket.model.SecurityUser;
import co.kremnev.mymarket.model.User;
import co.kremnev.mymarket.repository.UserRepository;
import co.kremnev.payment.client.api.PaymentsApi;
import co.kremnev.payment.client.model.CreateBalanceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PaymentsApi paymentsApi;
    private final ServerSecurityContextRepository securityContextRepository;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           PaymentsApi paymentsApi,
                           ServerSecurityContextRepository securityContextRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.paymentsApi = paymentsApi;
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Пользователь не найден: " + username)))
                .map(SecurityUser::new);
    }

    @Override
    public Mono<UserDetails> updatePassword(UserDetails user, String newPassword) {
        return userRepository.findByUsername(user.getUsername())
                .doOnNext(u -> u.setPassword(newPassword))
                .flatMap(userRepository::save)
                .map(SecurityUser::new);
    }

    @Override
    public Mono<User> registerUser(String username, String rawPassword) {
        var user = new User(username, passwordEncoder.encode(rawPassword));
        return userRepository.save(user)
                .onErrorMap(DataIntegrityViolationException.class,
                        e -> new IllegalArgumentException("Имя пользователя уже занято"))
                .flatMap(saved -> paymentsApi.createBalance(new CreateBalanceRequest().userId(saved.getId()))
                        .thenReturn(saved)
                        .onErrorResume(e -> {
                            log.error("Balance creation failed for user {}, rolling back", saved.getId(), e);
                            return userRepository.delete(saved)
                                    .then(Mono.error(new RuntimeException(
                                            "Ошибка регистрации: не удалось создать аккаунт. Попробуйте позже.")));
                        }));
    }

    @Override
    public Mono<Void> loginUser(User user, ServerWebExchange exchange) {
        var principal = new SecurityUser(user);
        var authentication = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());
        var securityContext = new SecurityContextImpl(authentication);
        return securityContextRepository.save(exchange, securityContext);
    }
}
