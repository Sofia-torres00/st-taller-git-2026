package py.edu.uc.lp3.service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import py.edu.uc.lp3.dto.ExplosionAmirResponse;
import reactor.core.publisher.Mono;

@Component
public class AmirExplosionClient {
    private final WebClient webClient;
    private final Duration timeout;

    public AmirExplosionClient(@Qualifier("minecraftRemotoWebClient") WebClient webClient,
            @Value("${minecraft-remoto.timeout:5s}") Duration timeout) {
        this.webClient = webClient;
        this.timeout = timeout;
    }

    public ExplosionAmirResponse simular(Long creeperId) {
        return webClient.post().uri("/api/lp3/explosiones/simular")
                .bodyValue(Map.of("creeperId", creeperId))
                .retrieve()
                .onStatus(status -> status.value() != 200, response -> {
                    int codigo = response.statusCode().value();
                    HttpStatus estado = switch (codigo) {
                        case 400 -> HttpStatus.BAD_REQUEST;
                        case 404 -> HttpStatus.NOT_FOUND;
                        case 409 -> HttpStatus.CONFLICT;
                        default -> HttpStatus.BAD_GATEWAY;
                    };
                    return Mono.error(new ServicioRemotoException("A3", "Amir", estado,
                            "Amir no confirmo la explosion (HTTP " + codigo + "); verifique su estado antes de repetir"));
                })
                .bodyToMono(ExplosionAmirResponse.class)
                .switchIfEmpty(Mono.error(invalida()))
                .map(resultado -> validar(creeperId, resultado))
                .timeout(timeout)
                .onErrorMap(TimeoutException.class, exception -> new ServicioRemotoException("A3", "Amir",
                        HttpStatus.GATEWAY_TIMEOUT,
                        "Amir no respondio a tiempo; la explosion puede haberse aplicado. Verifique antes de repetir", exception))
                .onErrorMap(exception -> !(exception instanceof ResponseStatusException), exception ->
                        new ServicioRemotoException("A3", "Amir", HttpStatus.BAD_GATEWAY,
                                "No se pudo confirmar la respuesta de Amir; verifique su estado antes de repetir", exception))
                .block();
    }

    private ExplosionAmirResponse validar(Long creeperId, ExplosionAmirResponse resultado) {
        if (resultado.radio() == null || !Double.isFinite(resultado.radio()) || resultado.radio() < 0
                || resultado.creeper() == null || !creeperId.equals(resultado.creeper().id())
                || !Boolean.TRUE.equals(resultado.creeper().eliminado())
                || resultado.creeper().vidaFinal() == null || resultado.objetivos() == null) {
            throw invalida();
        }
        for (var objetivo : resultado.objetivos()) {
            if (objetivo == null || objetivo.id() == null || objetivo.distancia() == null
                    || !Double.isFinite(objetivo.distancia()) || objetivo.distancia() < 0
                    || objetivo.vidaInicial() == null || objetivo.vidaInicial() < 0
                    || objetivo.danoRecibido() == null || objetivo.danoRecibido() < 0
                    || objetivo.vidaFinal() == null || objetivo.vidaFinal() < 0 || objetivo.eliminado() == null) {
                throw invalida();
            }
        }
        return resultado;
    }

    private ResponseStatusException invalida() {
        return new ServicioRemotoException("A3", "Amir", HttpStatus.BAD_GATEWAY,
                "Respuesta de Amir incompleta o no confirmable; verifique su estado antes de repetir");
    }
}
