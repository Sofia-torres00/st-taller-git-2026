package py.edu.uc.lp3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("API REST - Minecraft")
                        .version("1.0.0")
                        .description("Gestion de entidades, simulacion de explosiones y consulta de objetos externos."))
                .components(new Components()
                        .addSchemas("Error", new ObjectSchema()
                                .addProperty("error", new StringSchema().example("Descripcion del error"))
                                .addRequiredItem("error"))
                        .addResponses("DatosInvalidos", error("Parametros o cuerpo JSON invalidos"))
                        .addResponses("EntidadNoEncontrada", error("No existe una entidad con el identificador indicado"))
                        .addResponses("EstadoInvalido", error("El estado de la entidad impide realizar la operacion")));
    }

    private ApiResponse error(String descripcion) {
        return new ApiResponse().description(descripcion)
                .content(new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
                        new io.swagger.v3.oas.models.media.MediaType()
                                .schema(new Schema<>().$ref("#/components/schemas/Error"))));
    }
}
