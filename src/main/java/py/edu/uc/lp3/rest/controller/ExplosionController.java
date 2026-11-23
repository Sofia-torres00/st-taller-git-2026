package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.dto.ExplosionRequest;
import py.edu.uc.lp3.dto.ExplosionResponse;
import py.edu.uc.lp3.dto.ValidacionExplosionResponse;
import py.edu.uc.lp3.service.ExplosionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @PostMapping("/validar")
    @Operation(summary = "Validar una explosion sin efectos secundarios",
            description = "Solo lectura: no modifica H2, no aplica dano, no consume ni mata al Creeper, "
                    + "no elimina entidades ni guarda historial. Reutiliza las mismas validaciones que POST /api/explosiones: "
                    + "tres UUID distintos, participantes existentes de tipo Creeper, Jugador y Cerdo y Creeper vivo. "
                    + "No requiere que el jugador y el cerdo esten vivos ni que esten dentro del radio. "
                    + "No llama a Amir ni a Nicolas. Repetir la consulta conserva la base de datos. "
                    + "La aprobacion describe el estado consultado y no reserva una ejecucion futura.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
                    description = "El mismo cuerpo que recibe POST /api/explosiones",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExplosionRequest.class),
                            examples = @ExampleObject(name = "Participantes", value = """
                                    {"creeperId":"11111111-1111-4111-8111-111111111111",
                                     "jugadorId":"22222222-2222-4222-8222-222222222222",
                                     "cerdoId":"33333333-3333-4333-8333-333333333333"}
                                    """))))
    @ApiResponse(responseCode = "200", description = "Explosion aceptada; H2 permanece sin cambios",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidacionExplosionResponse.class),
                    examples = @ExampleObject(value = """
                            {"aceptada":true,"creeperId":"11111111-1111-4111-8111-111111111111",
                             "jugadorId":"22222222-2222-4222-8222-222222222222",
                             "cerdoId":"33333333-3333-4333-8333-333333333333"}
                            """)))
    @ApiResponse(responseCode = "400", description = "JSON o UUID invalidos, campos ausentes, IDs repetidos o tipos incorrectos",
            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error"),
                    examples = {
                        @ExampleObject(name = "IDs repetidos", value = "{\"error\":\"Las tres entidades deben tener identificadores distintos\"}"),
                        @ExampleObject(name = "Tipo incorrecto", value = "{\"error\":\"La entidad 11111111-1111-4111-8111-111111111111 debe ser de tipo Creeper\"}"),
                        @ExampleObject(name = "JSON o UUID invalido", value = "{\"error\":\"JSON inválido: revise los campos y el tipo de entidad\"}")
                    }))
    @ApiResponse(responseCode = "404", description = "Alguna entidad no existe",
            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error"),
                    examples = @ExampleObject(value = "{\"error\":\"No existe una entidad con id 11111111-1111-4111-8111-111111111111\"}")))
    @ApiResponse(responseCode = "409", description = "El estado del Creeper impide explotar",
            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error"),
                    examples = @ExampleObject(value = "{\"error\":\"Un creeper muerto no puede explotar\"}")))
    public ValidacionExplosionResponse validar(@RequestBody ExplosionRequest solicitud) {
        return service.validar(solicitud);
    }
}
