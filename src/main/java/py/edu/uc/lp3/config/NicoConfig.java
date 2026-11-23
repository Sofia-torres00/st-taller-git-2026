package py.edu.uc.lp3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class NicoConfig {
    @Bean
    public WebClient nicoWebClient(WebClient.Builder builder,
            @Value("${nico.base-url:http://100.83.179.97:8080}") String baseUrl) {
        return builder.baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).build();
    }
}
