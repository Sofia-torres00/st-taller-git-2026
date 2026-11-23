package py.edu.uc.lp3.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Combina el ataque de un mob local con las reglas de una entidad temporal remota",
        example = """
                {"atacanteId":"11111111-1111-1111-1111-111111111111","tipoObjetivo":"ZOMBIE","golpes":5}
                """)
public record CombateIntegradoRequest(
        @Schema(description = "UUID de un mob hostil local vivo", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID atacanteId,
        @Schema(description = "Tipo de entidad temporal que construira el servidor remoto",
                requiredMode = Schema.RequiredMode.REQUIRED) TipoObjetivoRemoto tipoObjetivo,
        @Schema(description = "Cantidad de golpes del mismo atacante; por defecto 1", minimum = "1", maximum = "100",
                defaultValue = "1") Integer golpes) {
}
