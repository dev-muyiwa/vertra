package com.vertra.adapters.security.config;

import com.vertra.domain.model.user.OAuthProvider;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "oauth")
public class OAuthConfig {

    private String redirectBaseUrl;  // e.g., https://api.vertra.app
    private ProviderConfig google = new ProviderConfig();
    private ProviderConfig github = new ProviderConfig();
    private ProviderConfig microsoft = new ProviderConfig();

    @Bean
    public RestClient oauthRestClient() {
        return RestClient.builder()
                .build();
    }

    public ProviderConfig getProviderConfig(OAuthProvider provider) {
        return switch (provider) {
            case GOOGLE -> google;
            case GITHUB -> github;
            case MICROSOFT -> microsoft;
        };
    }

    @Getter
    @Setter
    public static class ProviderConfig {
        private String clientId;
        private String clientSecret;
        private String authorizationUrl;
        private String tokenUrl;
        private String scope;
    }
}
