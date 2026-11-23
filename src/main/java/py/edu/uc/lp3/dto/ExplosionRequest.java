package py.edu.uc.lp3.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "IDs de tres entidades existentes y distintas; cada ID debe corresponder al tipo indicado")
public record ExplosionRequest(
        @Schema(description = "UUID de un creeper vivo", requiredMode = Schema.RequiredMode.REQUIRED,
                example = "11111111-1111-4111-8111-111111111111") UUID creeperId,
        @Schema(description = "UUID del jugador", requiredMode = Schema.RequiredMode.REQUIRED,
                example = "22222222-2222-4222-8222-222222222222") UUID jugadorId,
        @Schema(description = "UUID del cerdo", requiredMode = Schema.RequiredMode.REQUIRED,
                example = "33333333-3333-4333-8333-333333333333") UUID cerdoId) {
}
