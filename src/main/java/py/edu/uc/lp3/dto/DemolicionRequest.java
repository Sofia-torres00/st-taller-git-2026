package py.edu.uc.lp3.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Identificadores independientes de un explosivo en Nico y un Creeper en Amir")
public record DemolicionRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                example = "b26bb20b-a15f-43ed-b4a9-f2c19480516e") UUID explosivoNicoId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1", example = "25") Long creeperAmirId) {
}
