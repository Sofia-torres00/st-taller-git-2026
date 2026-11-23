package py.edu.uc.lp3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

@Configuration
public class MinecraftRemotoConfig {
    @Bean
    public WebClient minecraftRemotoWebClient(WebClient.Builder builder,
            @Value("${minecraft-remoto.base-url:http://100.78.121.31:8080}") String baseUrl) {
        return builder.baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().disableRetry(true)))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
