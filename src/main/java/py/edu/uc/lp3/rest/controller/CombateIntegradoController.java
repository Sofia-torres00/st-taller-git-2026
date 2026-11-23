package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.dto.CombateIntegradoRequest;
import py.edu.uc.lp3.dto.CombateIntegradoResponse;
import py.edu.uc.lp3.service.CombateIntegradoService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/combates/integrados")
@Tag(name = "Combates integrados", description = "Reglas de ataque locales combinadas con el Minecraft remoto")
public class CombateIntegradoController {
    private final CombateIntegradoService service;

    public CombateIntegradoController(CombateIntegradoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Simular un combate entre proyectos",
            description = "Calcula el danio del atacante local y lo envia con WebClient a GET /entidad/{tipo}?danio=... "
                    + "del proyecto remoto. El otro proyecto construye una entidad temporal, ejecuta su comportamiento "
                    + "y evalua vida, muerte y drops. CREEPER puede explotar durante ese comportamiento. "
                    + "No modifica entidades persistidas de ninguno de los dos proyectos.")
    @ApiResponse(responseCode = "200", description = "Ataque local y resultado de las reglas remotas")
    @ApiResponse(responseCode = "400", ref = "#/components/responses/DatosInvalidos")
    @ApiResponse(responseCode = "404", ref = "#/components/responses/EntidadNoEncontrada")
    @ApiResponse(responseCode = "409", ref = "#/components/responses/EstadoInvalido")
    @ApiResponse(responseCode = "502", description = "Error HTTP, de conexion o de respuesta del servidor remoto", content = @Content)
    @ApiResponse(responseCode = "504", description = "El servidor remoto supero el tiempo limite", content = @Content)
    public Mono<CombateIntegradoResponse> simular(@RequestBody CombateIntegradoRequest solicitud) {
        return service.simular(solicitud);
    }
}
