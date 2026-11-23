package py.edu.uc.lp3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import py.edu.uc.lp3.persistence.repository.DemolicionRepository;
import py.edu.uc.lp3.persistence.repository.EntidadRepository;
import py.edu.uc.lp3.domain.Demolicion;
import py.edu.uc.lp3.domain.Zombie;
import py.edu.uc.lp3.domain.Vector3D;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:demoliciones-test;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class DemolicionTests {
    private static final String ID_NICO = "b26bb20b-a15f-43ed-b4a9-f2c19480516e";
    private static final String SOLICITUD = "{\"explosivoNicoId\":\"" + ID_NICO + "\",\"creeperAmirId\":25}";
    private static final String NICO_OK = "{\"id\":\"" + ID_NICO
            + "\",\"clase\":\"Creeper\",\"interfaceDominio\":\"Explosivo\",\"puedeExplotar\":true}";
    private static final String AMIR_OK = """
            {"radio":3.0,"creeper":{"id":25,"cargado":false,"vidaFinal":0,"eliminado":true,"mensaje":"Explosion aplicada"},
             "objetivos":[{"id":13,"nombre":"Player","distancia":1.0,"vidaInicial":20,
             "danoRecibido":13,"vidaFinal":7,"eliminado":false}]}
            """;
    private static final AtomicInteger llamadasNico = new AtomicInteger();
    private static final AtomicInteger llamadasAmir = new AtomicInteger();
    private static final List<String> orden = new CopyOnWriteArrayList<>();
    private static volatile int codigoNico;
    private static volatile int codigoAmir;
    private static volatile String respuestaNico;
    private static volatile String respuestaAmir;
    private static volatile String cuerpoAmir;
    private static volatile String metodoNico;
    private static volatile String metodoAmir;
    private static volatile long demoraNico;
    private static volatile long demoraAmir;
    private static final HttpServer nico = servidor(true);
    private static final HttpServer amir = servidor(false);

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private DemolicionRepository repository;
    @Autowired private EntidadRepository entidades;
    @Autowired private JdbcTemplate jdbc;

    @DynamicPropertySource
    static void propiedades(DynamicPropertyRegistry registry) {
        registry.add("nico.base-url", () -> "http://127.0.0.1:" + nico.getAddress().getPort());
        registry.add("minecraft-remoto.base-url", () -> "http://127.0.0.1:" + amir.getAddress().getPort());
        registry.add("nico.timeout", () -> "3s");
        registry.add("minecraft-remoto.timeout", () -> "3s");
    }

    @BeforeEach
    void preparar() {
        repository.deleteAll();
        llamadasNico.set(0);
        llamadasAmir.set(0);
        orden.clear();
        codigoNico = 200;
        codigoAmir = 200;
        respuestaNico = NICO_OK;
        respuestaAmir = AMIR_OK;
        cuerpoAmir = null;
        demoraNico = 0;
        demoraAmir = 0;
    }

    @AfterAll
    static void detener() {
        nico.stop(0);
        amir.stop(0);
    }

    @Test
    void ejecutaEnOrdenYGuardaEnH2SoloLaExplosionConfirmada() throws Exception {
        var respuesta = mockMvc.perform(post("/api/demoliciones").contentType(MediaType.APPLICATION_JSON).content(SOLICITUD))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.explosivoNicoId").value(ID_NICO))
                .andExpect(jsonPath("$.creeperAmirId").value(25))
                .andExpect(jsonPath("$.resultadoAmir.radio").value(3.0))
                .andExpect(jsonPath("$.resultadoAmir.creeper.eliminado").value(true))
                .andExpect(jsonPath("$.resultadoAmir.objetivos[0].vidaFinal").value(7))
                .andReturn().getResponse();
        var json = objectMapper.readTree(respuesta.getContentAsString());
        assertEquals("/api/demoliciones/" + json.path("id").asText(), respuesta.getHeader("Location"));
        assertEquals(List.of("NICO", "AMIR"), orden);
        assertEquals("GET", metodoNico);
        assertEquals("POST", metodoAmir);
        assertEquals(objectMapper.readTree("{\"creeperId\":25}"), objectMapper.readTree(cuerpoAmir));
        assertEquals(1, repository.count());
        var guardada = repository.findById(UUID.fromString(json.path("id").asText())).orElseThrow();
        assertEquals(objectMapper.readTree(AMIR_OK), objectMapper.readTree(guardada.getResultadoAmir()));

        mockMvc.perform(get(respuesta.getHeader("Location"))).andExpect(status().isOk())
                .andExpect(jsonPath("$.resultadoAmir.creeper.id").value(25));
        mockMvc.perform(get("/api/demoliciones")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        assertEquals(1, llamadasNico.get());
        assertEquals(1, llamadasAmir.get());
        guardarEjemplo("feliz", 201, respuesta.getContentAsString());
        guardarEjemplo("historial-feliz", 200, mockMvc.perform(get("/api/demoliciones"))
                .andReturn().getResponse().getContentAsString());
    }

    @Test
    void siNicoNoAutorizaNoLlamaAAmirNiGuarda() throws Exception {
        respuestaNico = NICO_OK.replace("true", "false");
        ejecutarEsperando(409);
        assertEquals(0, llamadasAmir.get());
    }

    @ParameterizedTest
    @CsvSource({"A2,409", "A3,409", "A3,404"})
    void identificaElOrigenYConservaTodaLaBaseConHistorialPrevio(String fuente, int estado) throws Exception {
        entidades.saveAndFlush(new Zombie("Preexistente", new Vector3D(0, 64, 0)));
        repository.saveAndFlush(new Demolicion(UUID.randomUUID(), 25L, AMIR_OK));
        var antes = estadoBase();
        if (fuente.equals("A2")) {
            respuestaNico = NICO_OK.replace("true", "false");
        } else {
            codigoAmir = estado;
        }
        var respuesta = mockMvc.perform(post("/api/demoliciones").contentType(MediaType.APPLICATION_JSON).content(SOLICITUD))
                .andExpect(status().is(estado))
                .andExpect(jsonPath("$.fuente").value(fuente))
                .andExpect(jsonPath("$.servicio").value(fuente.equals("A2") ? "Nico" : "Amir"))
                .andExpect(jsonPath("$.mensaje").isString())
                .andExpect(jsonPath("$.status").value(estado)).andReturn().getResponse();
        assertEquals(4, objectMapper.readTree(respuesta.getContentAsString()).size());
        assertEquals(antes, estadoBase());
        assertEquals(1, repository.count());
        if (fuente.equals("A2")) assertEquals(0, llamadasAmir.get());
        if (estado == 409) {
            guardarEjemplo("rechazo-" + fuente.toLowerCase(), estado, respuesta.getContentAsString());
            guardarEjemplo("historial-rechazo-" + fuente.toLowerCase(), 200,
                    mockMvc.perform(get("/api/demoliciones")).andReturn().getResponse().getContentAsString());
        }
    }

    @Test
    void conservaLaVidaFinalInformadaSinImponerUnaConstanteNoDocumentada() throws Exception {
        respuestaAmir = AMIR_OK.replace("\"vidaFinal\":0", "\"vidaFinal\":7");
        mockMvc.perform(post("/api/demoliciones").contentType(MediaType.APPLICATION_JSON).content(SOLICITUD))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultadoAmir.creeper.vidaFinal").value(7));
        assertEquals(1, repository.count());
    }

    @ParameterizedTest
    @CsvSource({"404,404", "409,409", "500,502", "201,502"})
    void propagaRechazosDeNicoYDistingueErroresTecnicos(int remoto, int esperado) throws Exception {
        codigoNico = remoto;
        ejecutarEsperando(esperado);
        assertEquals(0, llamadasAmir.get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "", "{", "[]",
            "{\"id\":\"b26bb20b-a15f-43ed-b4a9-f2c19480516e\"}",
            "{\"id\":\"11111111-1111-1111-1111-111111111111\",\"puedeExplotar\":true}"})
    void noInterpretaUnaRespuestaIncompletaDeNicoComoAutorizacion(String json) throws Exception {
        respuestaNico = json;
        ejecutarEsperando(502);
        assertEquals(0, llamadasAmir.get());
    }

    @Test
    void unTimeoutDeNicoNoLlamaAAmir() throws Exception {
        demoraNico = 3500;
        ejecutarEsperando(504);
        assertEquals(0, llamadasAmir.get());
    }

    @ParameterizedTest
    @CsvSource({"400,400", "404,404", "409,409", "500,502", "201,502"})
    void noGuardaSiAmirRechazaODevuelveUnEstadoNoDocumentado(int remoto, int esperado) throws Exception {
        codigoAmir = remoto;
        ejecutarEsperando(esperado);
        assertEquals(1, llamadasAmir.get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "", "{", "[]"})
    void unHttp200SinConfirmacionDeAmirNoCreaUnRegistro(String json) throws Exception {
        respuestaAmir = json;
        ejecutarEsperando(502);
        assertEquals(1, llamadasAmir.get());
    }

    @Test
    void rechazaLaConfirmacionDeOtroCreeper() throws Exception {
        respuestaAmir = AMIR_OK.replace("\"id\":25", "\"id\":99");
        ejecutarEsperando(502);
    }

    @Test
    void rechazaUnCreeperNoEliminado() throws Exception {
        respuestaAmir = AMIR_OK.replace("\"eliminado\":true", "\"eliminado\":false");
        ejecutarEsperando(502);
    }

    @Test
    void noReintentaLaExplosionSiAmirNoResponde() throws Exception {
        demoraAmir = 3500;
        ejecutarEsperando(504);
        assertEquals(1, llamadasAmir.get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{", "null", "{\"creeperAmirId\":25}",
            "{\"explosivoNicoId\":\"no-es-uuid\",\"creeperAmirId\":25}",
            "{\"explosivoNicoId\":\"b26bb20b-a15f-43ed-b4a9-f2c19480516e\",\"creeperAmirId\":0}",
            "{\"explosivoNicoId\":\"b26bb20b-a15f-43ed-b4a9-f2c19480516e\",\"creeperAmirId\":-1}"})
    void validaAntesDeLlamarALosCompaneros(String json) throws Exception {
        mockMvc.perform(post("/api/demoliciones").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
        assertEquals(0, llamadasNico.get());
        assertEquals(0, llamadasAmir.get());
        assertEquals(0, repository.count());
    }

    @Test
    void consultaUnHistorialVacioYValidaLosIdsLocales() throws Exception {
        mockMvc.perform(get("/api/demoliciones")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        mockMvc.perform(get("/api/demoliciones/" + UUID.randomUUID())).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/demoliciones/invalido")).andExpect(status().isBadRequest());
        assertTrue(orden.isEmpty());
    }

    private void ejecutarEsperando(int estado) throws Exception {
        mockMvc.perform(post("/api/demoliciones").contentType(MediaType.APPLICATION_JSON).content(SOLICITUD))
                .andExpect(status().is(estado))
                .andExpect(jsonPath("$.fuente").value(llamadasAmir.get() == 0 ? "A2" : "A3"))
                .andExpect(jsonPath("$.servicio").value(llamadasAmir.get() == 0 ? "Nico" : "Amir"))
                .andExpect(jsonPath("$.mensaje").isString())
                .andExpect(jsonPath("$.status").value(estado));
        assertEquals(0, repository.count());
    }

    private void guardarEjemplo(String nombre, int estado, String cuerpo) throws Exception {
        Path carpeta = Path.of("target", "poo09-ejemplos");
        Files.createDirectories(carpeta);
        Files.writeString(carpeta.resolve(nombre + ".json"), objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(Map.of("status", estado, "body", objectMapper.readTree(cuerpo),
                        "procedencia", "Captura de DemolicionTests: A1 real, A2/A3 simulados y H2 en memoria")));
    }

    private Map<String, List<List<String>>> estadoBase() {
        Map<String, List<List<String>>> resultado = new LinkedHashMap<>();
        for (String sql : List.of("select * from entidades order by id", "select * from inventario_items order by entidad_id, posicion",
                "select * from demoliciones order by id")) {
            resultado.put(sql, jdbc.query(sql, (rs, fila) -> {
                List<String> valores = new ArrayList<>();
                for (int columna = 1; columna <= rs.getMetaData().getColumnCount(); columna++) {
                    Object valor = rs.getObject(columna);
                    valores.add(valor instanceof Clob clob ? clob.getSubString(1, (int) clob.length())
                            : valor == null ? null : valor.toString());
                }
                return valores;
            }));
        }
        return resultado;
    }

    private static HttpServer servidor(boolean esNico) {
        try {
            var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            String ruta = esNico ? "/api/minecraft/explosivos/" + ID_NICO : "/api/lp3/explosiones/simular";
            server.createContext(ruta, exchange -> {
                try (exchange) {
                    orden.add(esNico ? "NICO" : "AMIR");
                    if (esNico) {
                        llamadasNico.incrementAndGet();
                        metodoNico = exchange.getRequestMethod();
                    } else {
                        llamadasAmir.incrementAndGet();
                        metodoAmir = exchange.getRequestMethod();
                        cuerpoAmir = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    }
                    long demora = esNico ? demoraNico : demoraAmir;
                    String cuerpo = esNico ? respuestaNico : respuestaAmir;
                    int codigo = esNico ? codigoNico : codigoAmir;
                    if (demora > 0) {
                        try { Thread.sleep(demora); }
                        catch (InterruptedException exception) { Thread.currentThread().interrupt(); }
                    }
                    byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(codigo, bytes.length);
                    exchange.getResponseBody().write(bytes);
                } catch (IOException exception) {
                    // El cliente puede cerrar la conexion al superar el timeout de prueba.
                }
            });
            server.start();
            return server;
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }
}
