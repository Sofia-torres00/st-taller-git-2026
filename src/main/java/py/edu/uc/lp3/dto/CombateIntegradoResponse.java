package py.edu.uc.lp3.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import py.edu.uc.lp3.domain.TipoEntidad;

@Schema(description = "Ataque calculado por el dominio local y resultado evaluado por el servidor remoto")
public record CombateIntegradoResponse(UUID atacanteId, String nombreAtacante, TipoEntidad tipoAtacante,
        double danioPorGolpe, int golpes, int danioEnviado, ObjetivoRemoto objetivoRemoto) {

    @Schema(name = "ObjetivoCombateRemoto", description = "Estado final de la entidad temporal del otro proyecto")
    public record ObjetivoRemoto(String nombre, TipoObjetivoRemoto tipo, int vidaFinal, boolean viva,
            String mensaje, List<String> objetosSoltados) {
    }
}
