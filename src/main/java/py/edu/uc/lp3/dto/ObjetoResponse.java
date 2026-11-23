package py.edu.uc.lp3.dto;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Objeto devuelto por restful-api.dev")
public record ObjetoResponse(
        @Schema(example = "1") String id,
        @Schema(example = "Google Pixel 6 Pro") String name,
        @Schema(description = "Propiedades variables del objeto; puede ser null", nullable = true,
                example = "{\"color\":\"Cloudy White\",\"capacity\":\"128 GB\"}") Map<String, Object> data) {
}
