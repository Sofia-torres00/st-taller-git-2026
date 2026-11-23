package py.edu.uc.lp3.service;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import py.edu.uc.lp3.dto.ObjetoResponse;
import reactor.core.publisher.Mono;

@Service
public class ObjetoService {
    private final WebClient webClient;
    private final Duration timeout;

    public ObjetoService(@Qualifier("restfulApiWebClient") WebClient webClient,
            @Value("${restful-api.timeout:10s}") Duration timeout) {
        this.webClient = webClient;
        this.timeout = timeout;
    }

    public Mono<List<ObjetoResponse>> listar() {
        return webClient.get()
                .uri("/objects")
                .retrieve()
                .bodyToFlux(ObjetoResponse.class)
                .collectList()
                .timeout(timeout);
    }
}
