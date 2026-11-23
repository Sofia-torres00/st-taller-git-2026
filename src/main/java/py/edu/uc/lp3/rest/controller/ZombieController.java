package py.edu.uc.lp3.rest.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.TipoEntidad;
import py.edu.uc.lp3.domain.Vector3D;
import py.edu.uc.lp3.domain.Zombie;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Zombies", description = "Construccion de un zombie de ejemplo sin persistirlo")
public class ZombieController {

    @GetMapping("/api/zombie")
    @Operation(summary = "Construir un zombie con parametros de URL",
            description = "El nombre no puede estar vacio; las coordenadas deben ser finitas y la velocidad no negativa.")
    @ApiResponse(responseCode = "200", description = "Zombie construido; no se guarda en H2")
    @ApiResponse(responseCode = "400", description = "Parametros ausentes, de tipo incorrecto o rechazados por el dominio",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "object"),
                    examples = @ExampleObject(value = "{\"error\":\"La velocidad debe ser finita y no negativa\"}")))
    public ZombieResponse crearZombie(
            @Parameter(description = "Nombre del zombie", example = "Zombie1") @RequestParam String nombre,
            @Parameter(description = "Coordenada X", example = "0") @RequestParam double x,
            @Parameter(description = "Coordenada Y", example = "64") @RequestParam double y,
            @Parameter(description = "Coordenada Z", example = "0") @RequestParam double z,
            @Parameter(description = "Velocidad finita y no negativa", example = "1.5",
                    schema = @Schema(minimum = "0")) @RequestParam double velocidad) {
        Zombie zombie = new Zombie(nombre, new Vector3D(x, y, z), velocidad);

        return new ZombieResponse(
                zombie.getId(),
                zombie.getNombre(),
                zombie.getTipo(),
                zombie.getSalud(),
                zombie.getVelocidad(),
                zombie.estaViva(),
                zombie.obtenerPosicion(),
                zombie.describirComportamiento());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> valorInvalido(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }

    public record ZombieResponse(
            UUID id,
            String nombre,
            TipoEntidad tipo,
            double salud,
            double velocidad,
            boolean viva,
            Vector3D ubicacion,
            String comportamiento) {
    }
}
