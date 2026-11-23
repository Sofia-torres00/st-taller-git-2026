package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Inicio", description = "Presentacion de la API")
public class IndexController {

    @GetMapping("/")
    @Operation(summary = "Consultar la presentacion de la API")
    @ApiResponse(responseCode = "200", description = "Mensaje de bienvenida",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string",
                    example = "API REST - Sofia Torres - Minecraft")))
    public String inicio() {
        return "API REST - Sofia Torres - Minecraft";
    }
}
