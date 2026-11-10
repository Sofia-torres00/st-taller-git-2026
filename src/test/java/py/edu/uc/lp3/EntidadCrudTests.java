package py.edu.uc.lp3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import py.edu.uc.lp3.domain.TipoEntidad;
import py.edu.uc.lp3.dto.EntidadRequest;
import py.edu.uc.lp3.persistence.repository.EntidadRepository;
import py.edu.uc.lp3.service.EntidadService;

@SpringBootTest
@AutoConfigureMockMvc
class EntidadCrudTests {
    private static final String DATOS = """
            {"nombre":" Zombie1 ","tipo":"ZOMBIE","salud":20,"ubicacion":{"x":0,"y":64,"z":0}}
            """;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private EntidadRepository repository;
    @Autowired private EntidadService service;

    @BeforeEach
    void limpiarBase() {
        repository.deleteAll();
    }

    @Test
    void crudCompletoPersisteEnH2() throws Exception {
        mockMvc.perform(get("/api/entidades"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));

        String id = crear();
        assertEquals("Zombie1", repository.findById(UUID.fromString(id)).orElseThrow().getNombre());

        mockMvc.perform(get("/api/entidades/" + id))
                .andExpect(status().isOk()).andExpect(jsonPath("$.tipo").value("ZOMBIE"))
                .andExpect(jsonPath("$.ubicacion.y").value(64));
        mockMvc.perform(get("/api/entidades"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(put("/api/entidades/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(DATOS.replace("Zombie1", "Zombie2").replace("20", "0")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Zombie2"))
                .andExpect(jsonPath("$.salud").value(0));
        assertEquals("Zombie2", repository.findById(UUID.fromString(id)).orElseThrow().getNombre());
        mockMvc.perform(get("/api/entidades/" + id))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Zombie2"))
                .andExpect(jsonPath("$.salud").value(0));

        mockMvc.perform(delete("/api/entidades/" + id)).andExpect(status().isNoContent());
        assertFalse(repository.existsById(UUID.fromString(id)));
        mockMvc.perform(get("/api/entidades/" + id)).andExpect(status().isNotFound());
    }

    static Stream<String> datosInvalidos() {
        return Stream.of(
                DATOS.replace(" Zombie1 ", " "),
                DATOS.replace(" Zombie1 ", "x".repeat(101)),
                DATOS.replace("\"tipo\":\"ZOMBIE\"", "\"tipo\":null"),
                DATOS.replace("\"salud\":20", "\"salud\":-1"),
                DATOS.replace("\"salud\":20,", ""),
                DATOS.replace("{\"x\":0,\"y\":64,\"z\":0}", "null"),
                DATOS.replace("\"x\":0,", ""),
                DATOS.replace("\"x\":0", "\"x\":1e309"));
    }

    @ParameterizedTest
    @MethodSource("datosInvalidos")
    void rechazaDatosInvalidosSinPersistir(String datos) throws Exception {
        mockMvc.perform(post("/api/entidades").contentType(MediaType.APPLICATION_JSON).content(datos))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").isString());
        assertEquals(0, repository.count());
    }

    @Test
    void actualizacionInvalidaConservaDatos() throws Exception {
        String id = crear();
        mockMvc.perform(put("/api/entidades/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(DATOS.replace("\"salud\":20", "\"salud\":-1")))
                .andExpect(status().isBadRequest());
        assertEquals(20, repository.findById(UUID.fromString(id)).orElseThrow().getSalud());
    }

    @Test
    void operacionesSobreIdInexistenteDevuelven404() throws Exception {
        String ruta = "/api/entidades/" + UUID.randomUUID();
        mockMvc.perform(get(ruta)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("ENTIDAD_NO_ENCONTRADA"))
                .andExpect(jsonPath("$.error").isString());
        mockMvc.perform(put(ruta).contentType(MediaType.APPLICATION_JSON).content(DATOS))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete(ruta)).andExpect(status().isNotFound());
        assertEquals(0, repository.count());
    }

    @Test
    void rechazaJsonTipoEIdentificadorInvalidos() throws Exception {
        mockMvc.perform(post("/api/entidades").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").isString());
        mockMvc.perform(post("/api/entidades").contentType(MediaType.APPLICATION_JSON)
                .content(DATOS.replace("ZOMBIE", "INVALIDO")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").isString());
        mockMvc.perform(get("/api/entidades/no-es-uuid"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").isString());
    }

    @Test
    void servicioValidaAunqueNoSeInvoqueDesdeHttp() {
        assertThrows(IllegalArgumentException.class, () -> service.crear(new EntidadRequest(
                "Zombie", TipoEntidad.ZOMBIE, Double.NaN, new EntidadRequest.Ubicacion(0.0, 64.0, 0.0))));
        assertEquals(0, repository.count());
    }

    static Stream<Arguments> configuracionesValidas() {
        return Stream.of(
                Arguments.of("ZOMBIE", "{\"velocidad\":3}", "{\"velocidad\":5}", "velocidad", 3.0, 5.0),
                Arguments.of("CREEPER", "{\"fuseTime\":30}", "{\"fuseTime\":50}", "fuseTime", 30, 50),
                Arguments.of("ESQUELETO", "{\"tieneArco\":true}", "{\"tieneArco\":false}", "tieneArco", true, false),
                Arguments.of("ENDERMAN", "{\"puedeTomarBloques\":true}", "{\"puedeTomarBloques\":false}",
                        "puedeTomarBloques", true, false),
                Arguments.of("CERDO", "{\"edad\":20,\"montable\":true}", "{\"edad\":10,\"montable\":false}",
                        "edad", 20, 10),
                Arguments.of("ALDEANO", "{\"profesion\":\"Herrero\",\"nivelComercio\":3}",
                        "{\"profesion\":\"Cartografo\",\"nivelComercio\":4}", "profesion", "Herrero", "Cartografo"),
                Arguments.of("JUGADOR", "{\"capacidadInventario\":10,\"modoCreativo\":false}",
                        "{\"capacidadInventario\":10,\"modoCreativo\":true}", "modoCreativo", false, true));
    }

    @ParameterizedTest
    @MethodSource("configuracionesValidas")
    void creaYActualizaAtributosDeCadaSubclase(String tipo, String iniciales, String nuevos,
            String campo, Object valorInicial, Object valorNuevo) throws Exception {
        String plantilla = """
                {"nombre":"Personaje","tipo":"%s","salud":10,
                 "ubicacion":{"x":0,"y":64,"z":0},"atributos":%s}
                """;
        var creado = mockMvc.perform(post("/api/entidades").contentType(MediaType.APPLICATION_JSON)
                .content(plantilla.formatted(tipo, iniciales)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.atributos." + campo).value(valorInicial))
                .andReturn();
        String id = objectMapper.readTree(creado.getResponse().getContentAsString()).get("id").asText();
        mockMvc.perform(put("/api/entidades/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(plantilla.formatted(tipo, nuevos)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.atributos." + campo).value(valorNuevo));
        mockMvc.perform(get("/api/entidades/" + id))
                .andExpect(status().isOk()).andExpect(jsonPath("$.atributos." + campo).value(valorNuevo));
    }

    static Stream<String> atributosInvalidos() {
        return Stream.of(
                DATOS.strip().replace("20", "21"),
                conAtributos("ZOMBIE", "{\"velocidad\":-1}"),
                conAtributos("CREEPER", "{\"fuseTime\":0}"),
                conAtributos("CERDO", "{\"edad\":-1}"),
                conAtributos("CERDO", "{\"velocidad\":3}"),
                conAtributos("ALDEANO", "{\"nivelComercio\":6}"),
                conAtributos("ALDEANO", "{\"profesion\":\" \"}"),
                conAtributos("ALDEANO", "{\"profesion\":\"" + "x".repeat(256) + "\"}"),
                conAtributos("JUGADOR", "{\"capacidadInventario\":0}"));
    }

    @ParameterizedTest
    @MethodSource("atributosInvalidos")
    void rechazaAtributosInvalidosDeLasSubclases(String datos) throws Exception {
        mockMvc.perform(post("/api/entidades").contentType(MediaType.APPLICATION_JSON).content(datos))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").isString());
        assertEquals(0, repository.count());
        mockMvc.perform(get("/api/entidades")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void noPermiteCambiarSubclaseYRevierteActualizacionesInvalidas() throws Exception {
        String id = crear();
        mockMvc.perform(put("/api/entidades/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(DATOS.replace("ZOMBIE", "CERDO"))).andExpect(status().isBadRequest());
        mockMvc.perform(put("/api/entidades/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(conAtributos("ZOMBIE", "{\"velocidad\":-1}").replace("Zombie1", "OtroNombre")))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/entidades/" + id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Zombie1"))
                .andExpect(jsonPath("$.tipo").value("ZOMBIE"))
                .andExpect(jsonPath("$.atributos.velocidad").value(1.5));
    }

    private static String conAtributos(String tipo, String atributos) {
        String base = DATOS.strip().replace("ZOMBIE", tipo);
        return base.substring(0, base.length() - 1) + ",\"atributos\":" + atributos + "}";
    }

    private String crear() throws Exception {
        var resultado = mockMvc.perform(post("/api/entidades").contentType(MediaType.APPLICATION_JSON).content(DATOS))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nombre").value("Zombie1")).andReturn();
        String id = objectMapper.readTree(resultado.getResponse().getContentAsString()).get("id").asText();
        assertEquals("/api/entidades/" + id, resultado.getResponse().getHeader("Location"));
        return id;
    }
}
