package com.vertra.adapters.security.config;

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

    private ProviderConfig google = new ProviderConfig();
    private ProviderConfig github = new ProviderConfig();
    private ProviderConfig microsoft = new ProviderConfig();

    @Bean
    public RestClient oauthRestClient() {
        return RestClient.builder()
                .build();
    }

    @Getter
    @Setter
    public static class ProviderConfig {
        private String clientId;
        private String clientSecret;
        private String tokenInfoUrl;
        private String userInfoUrl;
    }
}
