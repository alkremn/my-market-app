package co.kremnev.mymarket.config;

import co.kremnev.payment.client.ApiClient;
import co.kremnev.payment.client.api.PaymentsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    @Bean
    public PaymentsApi webClient(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
        var oauth2Filter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2Filter.setDefaultClientRegistrationId("payment-client");

        WebClient webClient = WebClient.builder()
                .filter(oauth2Filter)
                .build();

        ApiClient apiClient = new ApiClient(webClient);
        apiClient.setBasePath(paymentServiceUrl);
        return new PaymentsApi(apiClient);
    }
}
