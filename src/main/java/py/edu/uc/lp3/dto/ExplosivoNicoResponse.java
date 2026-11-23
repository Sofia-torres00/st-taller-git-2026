package py.edu.uc.lp3.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Contrato ExplosivoRespuesta de Nico, GET /api/minecraft/explosivos/{id}")
public record ExplosivoNicoResponse(UUID id, String clase, String interfaceDominio, Boolean puedeExplotar) {
}
