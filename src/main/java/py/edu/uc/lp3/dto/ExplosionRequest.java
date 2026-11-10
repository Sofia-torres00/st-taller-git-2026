package py.edu.uc.lp3.dto;

import java.util.UUID;

public record ExplosionRequest(UUID creeperId, UUID jugadorId, UUID cerdoId) {
}
