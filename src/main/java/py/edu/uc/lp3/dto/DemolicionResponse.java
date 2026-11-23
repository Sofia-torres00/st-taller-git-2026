package py.edu.uc.lp3.dto;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Demolicion confirmada por Amir y registrada en H2 por Sofi")
public record DemolicionResponse(UUID id, UUID explosivoNicoId, Long creeperAmirId, Instant fecha,
        ExplosionAmirResponse resultadoAmir) {
}
