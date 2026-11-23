package py.edu.uc.lp3.rest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.TipoEntidad;
import py.edu.uc.lp3.domain.Vector3D;
import py.edu.uc.lp3.domain.Zombie;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Comportamientos", description = "Ejemplos de comportamiento de las entidades")
public class ComportamientoController {

    @GetMapping("/api/comportamientos")
    @Operation(summary = "Obtener los comportamientos de un zombie y un creeper")
    @ApiResponse(responseCode = "200", description = "Comportamientos de las dos entidades de ejemplo")
    public ComportamientosResponse obtenerComportamientos() {
        Entidad zombie = new Zombie("Zombie1", new Vector3D(0, 64, 0), 1.5);
        Entidad creeper = new Creeper("Creeper1", new Vector3D(5, 64, 5), 3);

        List<EntidadResponse> entidades = List.of(zombie, creeper).stream()
                .map(this::crearRespuesta)
                .toList();

        return new ComportamientosResponse(entidades);
    }

    private EntidadResponse crearRespuesta(Entidad entidad) {
        return new EntidadResponse(
                entidad.getNombre(),
                entidad.getTipo(),
                entidad.describirComportamiento());
    }

    public record ComportamientosResponse(List<EntidadResponse> entidades) {
    }

    @Schema(name = "ComportamientoEntidadResponse", description = "Comportamiento de una entidad de ejemplo")
    public record EntidadResponse(
            String nombre,
            TipoEntidad tipo,
            String comportamiento) {
    }
}
