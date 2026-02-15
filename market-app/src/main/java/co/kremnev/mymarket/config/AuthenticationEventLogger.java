package co.kremnev.mymarket.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEventLogger {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationEventLogger.class);

    @EventListener
    public void onAuthFailure(AbstractAuthenticationFailureEvent event) {
        log.warn("AUTH FAILURE: {} — reason: {}", event.getAuthentication().getName(),
                event.getException().getMessage());
    }
}
