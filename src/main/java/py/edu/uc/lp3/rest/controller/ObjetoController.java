package py.edu.uc.lp3.rest.controller;

import java.util.List;
import java.util.concurrent.TimeoutException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.server.ResponseStatusException;

import py.edu.uc.lp3.dto.ObjetoResponse;
import py.edu.uc.lp3.service.ObjetoService;
import reactor.core.publisher.Mono;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/objetos")
@Tag(name = "Objetos externos", description = "Consulta de restful-api.dev mediante WebClient")
public class ObjetoController {
    private final ObjetoService service;

    public ObjetoController(ObjetoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar objetos de la API externa",
            description = "Consulta /objects sin persistir los resultados. El campo data tiene propiedades variables y puede ser nulo.")
    @ApiResponse(responseCode = "200", description = "Lista de objetos externos")
    @ApiResponse(responseCode = "502", description = "Fallo de conexion o error HTTP de la API externa", content = @Content)
    @ApiResponse(responseCode = "504", description = "La API externa supero el tiempo limite configurado", content = @Content)
    public Mono<List<ObjetoResponse>> listar() {
        return service.listar()
                .onErrorMap(WebClientException.class, exception -> new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY, "No se pudo consultar la API de objetos", exception))
                .onErrorMap(TimeoutException.class, exception -> new ResponseStatusException(
                        HttpStatus.GATEWAY_TIMEOUT, "La API de objetos no respondio a tiempo", exception));
    }
}
