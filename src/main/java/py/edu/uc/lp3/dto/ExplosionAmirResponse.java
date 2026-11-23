package py.edu.uc.lp3.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Contrato ExplosionResultado de Amir, POST /api/lp3/explosiones/simular")
public record ExplosionAmirResponse(Double radio, EstadoCreeper creeper, List<EstadoObjetivo> objetivos) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(name = "CreeperExplosionAmir")
    public record EstadoCreeper(Long id, Boolean cargado, Integer vidaFinal, Boolean eliminado, String mensaje) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(name = "ObjetivoExplosionAmir")
    public record EstadoObjetivo(Long id, String nombre, Double distancia, Integer vidaInicial,
            Integer danoRecibido, Integer vidaFinal, Boolean eliminado) {
    }
}
