package py.edu.uc.lp3.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Aprobacion de las reglas actuales de explosion. No aplica dano ni modifica H2.")
public record ValidacionExplosionResponse(
        @Schema(description = "Siempre true cuando se cumplen las reglas", example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED) boolean aceptada,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID creeperId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID jugadorId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID cerdoId) {
}
