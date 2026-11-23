package py.edu.uc.lp3.rest.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.dto.DemolicionRequest;
import py.edu.uc.lp3.dto.DemolicionResponse;
import py.edu.uc.lp3.service.DemolicionService;

@RestController
@RequestMapping("/api/demoliciones")
@Tag(name = "Demoliciones coordinadas", description = "Sofi coordina la disponibilidad de Nico y la explosion de Amir")
public class DemolicionController {
    private final DemolicionService service;

    public DemolicionController(DemolicionService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Ejecutar una demolicion coordinada con Nico y Amir",
            description = "Consulta GET /api/minecraft/explosivos/{id} de Nico. Solo con puedeExplotar=true "
                    + "envia POST /api/lp3/explosiones/simular a Amir con creeperId. Amir aplica dano real "
                    + "y elimina su Creeper. Sofi guarda en H2 solo las explosiones confirmadas. "
                    + "Los dos IDs identifican entidades independientes. No hay reintentos automaticos ni rollback remoto.")
    @ApiResponse(responseCode = "201", description = "Explosion confirmada por Amir y registrada por Sofi")
    @ApiResponse(responseCode = "400", description = "Solicitud invalida o rechazada por Amir", content = @Content)
    @ApiResponse(responseCode = "404", description = "Explosivo de Nico o Creeper de Amir inexistente", content = @Content)
    @ApiResponse(responseCode = "409", description = "Nico no autoriza el explosivo o Amir rechaza la explosion", content = @Content)
    @ApiResponse(responseCode = "502", description = "Fallo HTTP, de conexion o de contrato remoto; verificar Amir antes de repetir", content = @Content)
    @ApiResponse(responseCode = "504", description = "Tiempo de espera agotado; el resultado en Amir puede ser incierto", content = @Content)
    @ApiResponse(responseCode = "503", description = "Amir confirmo la explosion pero fallo el registro en H2", content = @Content)
    public ResponseEntity<DemolicionResponse> ejecutar(@RequestBody DemolicionRequest solicitud) {
        var resultado = service.ejecutar(solicitud);
        return ResponseEntity.created(URI.create("/api/demoliciones/" + resultado.id())).body(resultado);
    }

    @GetMapping
    @Operation(summary = "Listar demoliciones confirmadas y guardadas por Sofi")
    @ApiResponse(responseCode = "200", description = "Historial local; no consulta las APIs de Nico ni de Amir")
    public List<DemolicionResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar una demolicion guardada por su UUID local")
    @ApiResponse(responseCode = "200", description = "Demolicion confirmada")
    @ApiResponse(responseCode = "400", ref = "#/components/responses/DatosInvalidos")
    @ApiResponse(responseCode = "404", description = "Registro local inexistente", content = @Content)
    public DemolicionResponse obtener(@PathVariable UUID id) {
        return service.obtener(id);
    }
}
