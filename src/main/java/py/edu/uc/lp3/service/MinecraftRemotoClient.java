package py.edu.uc.lp3.service;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import py.edu.uc.lp3.dto.CombateIntegradoResponse.ObjetivoRemoto;
import py.edu.uc.lp3.dto.TipoObjetivoRemoto;
import reactor.core.publisher.Mono;

@Component
public class MinecraftRemotoClient {
    private final WebClient webClient;
    private final Duration timeout;

    public MinecraftRemotoClient(@Qualifier("minecraftRemotoWebClient") WebClient webClient,
            @Value("${minecraft-remoto.timeout:5s}") Duration timeout) {
        this.webClient = webClient;
        this.timeout = timeout;
    }

    public Mono<ObjetivoRemoto> recibirDanio(TipoObjetivoRemoto tipo, int danio) {
        return webClient.get()
                .uri(builder -> builder.path("/entidad/{tipo}").queryParam("danio", danio)
                        .build(tipo.name().toLowerCase(Locale.ROOT)))
                .retrieve()
                .bodyToMono(DemostracionRemota.class)
                .switchIfEmpty(Mono.error(respuestaInvalida()))
                .map(respuesta -> convertir(tipo, respuesta))
                .timeout(timeout)
                .onErrorMap(TimeoutException.class, exception -> new ResponseStatusException(
                        HttpStatus.GATEWAY_TIMEOUT, "El servidor Minecraft remoto no respondio a tiempo", exception))
                .onErrorMap(exception -> !(exception instanceof ResponseStatusException),
                        exception -> new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                                "No se pudo consultar el servidor Minecraft remoto", exception));
    }

    private ObjetivoRemoto convertir(TipoObjetivoRemoto tipo, DemostracionRemota respuesta) {
        EntidadRemota entidad = respuesta.entidad();
        if (entidad == null || entidad.nombre() == null || !tipo.name().equals(entidad.tipo())
                || respuesta.vidaFinal() == null || respuesta.vidaFinal() < 0 || respuesta.viva() == null
                || respuesta.viva() != (respuesta.vidaFinal() > 0) || respuesta.recibirDanio() == null) {
            throw respuestaInvalida();
        }
        List<String> objetos = !respuesta.viva() && entidad.drop() != null ? entidad.drop() : List.of();
        return new ObjetivoRemoto(entidad.nombre(), tipo, respuesta.vidaFinal(), respuesta.viva(),
                respuesta.recibirDanio(), objetos);
    }

    private ResponseStatusException respuestaInvalida() {
        return new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Respuesta invalida del servidor Minecraft remoto");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DemostracionRemota(EntidadRemota entidad, String recibirDanio, Integer vidaFinal, Boolean viva) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EntidadRemota(String nombre, String tipo, List<String> drop) {
    }
}
