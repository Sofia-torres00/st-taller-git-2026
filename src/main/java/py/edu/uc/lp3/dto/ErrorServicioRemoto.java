package py.edu.uc.lp3.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Origen del rechazo o fallo remoto; status coincide con el codigo HTTP de Sofi")
public record ErrorServicioRemoto(
        @Schema(example = "A2", allowableValues = {"A2", "A3"}) String fuente,
        @Schema(example = "Nico") String servicio,
        @Schema(example = "Nico indica que el explosivo no puede explotar") String mensaje,
        @Schema(example = "409") int status) {
}
