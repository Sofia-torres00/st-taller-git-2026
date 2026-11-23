package py.edu.uc.lp3.service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import py.edu.uc.lp3.dto.ExplosivoNicoResponse;
import reactor.core.publisher.Mono;

@Component
public class NicoClient {
    private final WebClient webClient;
    private final Duration timeout;

    public NicoClient(@Qualifier("nicoWebClient") WebClient webClient,
            @Value("${nico.timeout:5s}") Duration timeout) {
        this.webClient = webClient;
        this.timeout = timeout;
    }

    public ExplosivoNicoResponse consultar(UUID id) {
        return webClient.get().uri("/api/minecraft/explosivos/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() != 200, response -> {
                    int codigo = response.statusCode().value();
                    HttpStatus estado = codigo == 404 ? HttpStatus.NOT_FOUND
                            : codigo == 409 ? HttpStatus.CONFLICT : HttpStatus.BAD_GATEWAY;
                    return Mono.error(new ServicioRemotoException("A2", "Nico", estado,
                            "Nico rechazo la consulta del explosivo (HTTP " + codigo + ")"));
                })
                .bodyToMono(ExplosivoNicoResponse.class)
                .switchIfEmpty(Mono.error(invalida()))
                .map(resultado -> {
                    if (!id.equals(resultado.id()) || resultado.puedeExplotar() == null) {
                        throw invalida();
                    }
                    return resultado;
                })
                .timeout(timeout)
                .onErrorMap(TimeoutException.class, exception -> new ServicioRemotoException("A2", "Nico",
                        HttpStatus.GATEWAY_TIMEOUT, "Nico no respondio a tiempo; no se llamo a Amir", exception))
                .onErrorMap(exception -> !(exception instanceof ResponseStatusException), exception ->
                        new ServicioRemotoException("A2", "Nico", HttpStatus.BAD_GATEWAY, "No se pudo consultar a Nico", exception))
                .block();
    }

    private ResponseStatusException invalida() {
        return new ServicioRemotoException("A2", "Nico", HttpStatus.BAD_GATEWAY, "Respuesta de Nico incompleta o invalida");
    }
}
