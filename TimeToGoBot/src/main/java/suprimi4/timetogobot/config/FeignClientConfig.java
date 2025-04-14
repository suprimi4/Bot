package suprimi4.timetogobot.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {
    @Value("${api.secret}")
    private String apiSecret;

    @Bean
    public RequestInterceptor apiKeyInterceptor() {
        return requestTemplate -> {
            if (requestTemplate.url().contains("/geocode/api/") ||
                    requestTemplate.url().contains("/routing/api/")) {
                requestTemplate.header("X-MY-API-KEY", apiSecret);
            }
        };
    }
}
