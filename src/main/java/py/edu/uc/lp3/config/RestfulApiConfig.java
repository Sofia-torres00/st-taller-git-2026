package py.edu.uc.lp3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RestfulApiConfig {
    @Bean
    public WebClient restfulApiWebClient(WebClient.Builder builder,
            @Value("${restful-api.base-url:https://api.restful-api.dev}") String baseUrl) {
        return builder.baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
