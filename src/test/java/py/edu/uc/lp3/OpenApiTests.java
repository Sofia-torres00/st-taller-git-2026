package py.edu.uc.lp3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiTests {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired @Qualifier("requestMappingHandlerMapping") private RequestMappingHandlerMapping mappings;

    @Test
    void documentaTodasLasOperacionesRestConResumenYRespuestas() throws Exception {
        JsonNode documento = documento();
        assertTrue(documento.path("openapi").asText().startsWith("3."));
        assertEquals("API REST - Minecraft", documento.at("/info/title").asText());

        mappings.getHandlerMethods().forEach((mapping, handler) -> {
            if (!handler.getBeanType().getPackageName().equals("py.edu.uc.lp3.rest.controller")) {
                return;
            }
            for (String ruta : mapping.getPatternValues()) {
                for (var metodo : mapping.getMethodsCondition().getMethods()) {
                    JsonNode operacion = documento.path("paths").path(ruta)
                            .path(metodo.name().toLowerCase(Locale.ROOT));
                    String contexto = metodo + " " + ruta;
                    assertFalse(operacion.isMissingNode(), contexto);
                    assertFalse(operacion.path("summary").asText().isBlank(), contexto);
                    assertFalse(operacion.path("tags").isEmpty(), contexto);
                    assertFalse(operacion.path("responses").isEmpty(), contexto);
                }
            }
        });
        assertFalse(documento.path("paths").has("/error"));
    }

    @Test
    void documentaCodigosHttpYEsquemasSinConfundirLasEntidadesConLosComportamientos() throws Exception {
        JsonNode documento = documento();
        JsonNode entidades = documento.path("paths").path("/api/entidades");
        JsonNode creacion = entidades.path("post").path("responses");
        assertTrue(creacion.has("201"));
        assertFalse(creacion.has("200"));
        assertTrue(creacion.path("201").path("headers").has("Location"));
        assertEquals("#/components/responses/DatosInvalidos", creacion.path("400").path("$ref").asText());
        assertFalse(entidades.path("get").path("responses").has("409"));

        JsonNode eliminacion = documento.path("paths").path("/api/entidades/{id}").path("delete").path("responses");
        assertTrue(eliminacion.has("204"));
        assertFalse(eliminacion.path("204").has("content"));
        assertTrue(eliminacion.has("400"));
        assertTrue(eliminacion.has("404"));

        JsonNode explosion = documento.path("paths").path("/api/explosiones").path("post").path("responses");
        for (String codigo : Set.of("200", "400", "404", "409")) {
            assertTrue(explosion.has(codigo), codigo);
        }
        JsonNode objetos = documento.path("paths").path("/api/objetos").path("get").path("responses");
        assertTrue(objetos.has("502"));
        assertTrue(objetos.has("504"));
        JsonNode combate = documento.path("paths").path("/api/combates/integrados").path("post").path("responses");
        for (String codigo : Set.of("200", "400", "404", "409", "502", "504")) {
            assertTrue(combate.has(codigo), codigo);
        }

        JsonNode esquemas = documento.at("/components/schemas");
        JsonNode demolicion = documento.path("paths").path("/api/demoliciones").path("post").path("responses");
        for (String codigo : Set.of("201", "400", "404", "409", "502", "503", "504")) {
            assertTrue(demolicion.has(codigo), codigo);
        }
        assertTrue(documento.path("paths").path("/api/demoliciones/{id}").has("get"));
        assertTrue(esquemas.path("EntidadResponse").path("properties").has("id"));
        assertTrue(esquemas.path("EntidadResponse").path("properties").has("atributos"));
        assertTrue(esquemas.path("ComportamientoEntidadResponse").path("properties").has("comportamiento"));
        assertFalse(esquemas.path("ComportamientoEntidadResponse").path("properties").has("id"));
        assertTrue(esquemas.path("ObjetoResponse").path("properties").has("data"));
    }

    @Test
    void documentaParametrosYCamposObligatoriosConEjemplos() throws Exception {
        JsonNode documento = documento();
        JsonNode parametros = documento.path("paths").path("/api/zombie").path("get").path("parameters");
        assertEquals(5, parametros.size());
        for (JsonNode parametro : parametros) {
            assertTrue(parametro.path("required").asBoolean());
            assertTrue(parametro.has("example"));
        }
        JsonNode esquemas = documento.at("/components/schemas");
        JsonNode entidad = esquemas.path("EntidadRequest");
        assertEquals(4, entidad.path("required").size());
        assertTrue(entidad.has("example"));
        assertEquals(3, esquemas.path("ExplosionRequest").path("required").size());
        assertEquals(3, esquemas.path("Ubicacion").path("required").size());
    }

    @Test
    void swaggerUiYSuConfiguracionEstanDisponibles() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/swagger-ui/index.html"));
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Swagger UI")));
        mockMvc.perform(get("/swagger-ui/swagger-ui-bundle.js"))
                .andExpect(status().isOk());
        JsonNode configuracion = objectMapper.readTree(mockMvc.perform(get("/v3/api-docs/swagger-config"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals("/v3/api-docs", configuracion.path("url").asText());
    }

    @Test
    void publicaLaEspecificacionEnYaml() throws Exception {
        mockMvc.perform(get("/v3/api-docs.yaml"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("openapi:")));
    }

    private JsonNode documento() throws Exception {
        return objectMapper.readTree(mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString());
    }
}
