package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.dto.ExplosionRequest;
import py.edu.uc.lp3.dto.ExplosionResponse;
import py.edu.uc.lp3.service.ExplosionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/explosiones")
@Tag(name = "Explosiones", description = "Simulacion transaccional de explosiones de creepers")
public class ExplosionController {
    private final ExplosionService service;

    public ExplosionController(ExplosionService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Simular una explosion",
            description = "Recibe los IDs distintos de un creeper vivo, un jugador y un cerdo. "
                    + "Aplica hasta 20 puntos de danio dentro de un radio de 6 bloques y consume la salud del creeper.")
    @ApiResponse(responseCode = "200", description = "Explosion realizada y cambios guardados")
    @ApiResponse(responseCode = "400", ref = "#/components/responses/DatosInvalidos")
    @ApiResponse(responseCode = "404", ref = "#/components/responses/EntidadNoEncontrada")
    @ApiResponse(responseCode = "409", ref = "#/components/responses/EstadoInvalido")
    public ExplosionResponse simular(@RequestBody ExplosionRequest solicitud) {
        return service.simular(solicitud);
    }
}
