package py.edu.uc.lp3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Clob;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import py.edu.uc.lp3.domain.Cerdo;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Demolicion;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Vector3D;
import py.edu.uc.lp3.domain.Zombie;
import py.edu.uc.lp3.dto.ExplosionRequest;
import py.edu.uc.lp3.dto.ValidacionExplosionResponse;
import py.edu.uc.lp3.persistence.repository.DemolicionRepository;
import py.edu.uc.lp3.persistence.repository.EntidadRepository;
import py.edu.uc.lp3.service.AmirExplosionClient;
import py.edu.uc.lp3.service.MinecraftRemotoClient;
import py.edu.uc.lp3.service.NicoClient;
import py.edu.uc.lp3.service.ObjetoService;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:validacion-explosion-test;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class ValidacionExplosionTests {
    private static final String RUTA = "/api/explosiones/validar";
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private EntidadRepository entidades;
    @Autowired private DemolicionRepository historial;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private EntityManagerFactory entityManagerFactory;
    @MockitoBean private NicoClient nico;
    @MockitoBean private AmirExplosionClient amir;
    @MockitoBean private MinecraftRemotoClient minecraftRemoto;
    @MockitoBean private ObjetoService objetos;

    private Creeper creeper;
    private Creeper muerto;
    private Jugador jugador;
    private Cerdo cerdo;
    private Zombie zombie;
    private Map<String, List<List<String>>> estadoAnterior;
    private Statistics estadisticas;

    @BeforeEach
    void prepararParticipantesEHistorialPrevio() {
        historial.deleteAll();
        entidades.deleteAll();
        creeper = new Creeper("Creeper vivo", new Vector3D(0, 64, 0), 30);
        muerto = new Creeper("Creeper muerto", new Vector3D(0, 64, 0), 30);
        muerto.tomarDanio(20);
        jugador = new Jugador("Jugador", new Vector3D(1, 64, 0), 36, false);
        cerdo = new Cerdo("Cerdo", new Vector3D(2, 64, 0), false, 0);
        zombie = new Zombie("Tipo incorrecto", new Vector3D(0, 64, 0));
        entidades.saveAll(List.of(creeper, muerto, jugador, cerdo, zombie));
        historial.saveAndFlush(new Demolicion(UUID.randomUUID(), 25L, """
                {"radio":3,"creeper":{"id":25,"cargado":false,"vidaFinal":0,
                 "eliminado":true,"mensaje":"Historial previo"},"objetivos":[]}
                """));
        estadisticas = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        estadisticas.setStatisticsEnabled(true);
        capturarReferencia();
    }

    @AfterEach
    void confirmaQueTodaLaBasePermaneceIgualYSinLlamadasExternas() {
        assertEquals(estadoAnterior, estadoBase(), "Las filas completas de entidades, inventarios e historial deben conservarse");
        assertEquals(0, estadisticas.getEntityInsertCount(), "No debe insertar entidades");
        assertEquals(0, estadisticas.getEntityUpdateCount(), "No debe actualizar entidades");
        assertEquals(0, estadisticas.getEntityDeleteCount(), "No debe eliminar entidades");
        verifyNoInteractions(nico, amir, minecraftRemoto, objetos);
    }

    @Test
    void apruebaRepetidamenteConElCuerpoExactoSinConsumirElCreeper() throws Exception {
        var esperado = objectMapper.valueToTree(new ValidacionExplosionResponse(true,
                creeper.getId(), jugador.getId(), cerdo.getId()));
        for (int intento = 0; intento < 5; intento++) {
            var respuesta = mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(json(solicitud())))
                    .andExpect(status().isOk()).andReturn().getResponse();
            assertEquals(esperado, objectMapper.readTree(respuesta.getContentAsString()));
        }
        assertEquals(20, entidades.findById(creeper.getId()).orElseThrow().getSalud());
        assertEquals(20, entidades.findById(jugador.getId()).orElseThrow().getSalud());
        assertEquals(10, entidades.findById(cerdo.getId()).orElseThrow().getSalud());
    }

    @Test
    void conservaElEstadoDeUnCreeperEncendido() throws Exception {
        creeper.encender();
        entidades.saveAndFlush(creeper);
        capturarReferencia();
        enviar(solicitud(), 200, null);
    }

    @Test
    void noAgregaRestriccionesDeRadioNiDeVidaDeLosObjetivos() throws Exception {
        jugador.tomarDanio(20);
        cerdo.tomarDanio(10);
        jugador.mover(1000, 64, 0);
        cerdo.mover(2000, 64, 0);
        entidades.saveAll(List.of(jugador, cerdo));
        capturarReferencia();
        enviar(solicitud(), 200, null);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{", "null", "{\"creeperId\":null}",
            "{\"creeperId\":\"UUID-invalido\"}",
            "{\"jugadorId\":\"UUID-invalido\"}", "{\"cerdoId\":\"UUID-invalido\"}"})
    void rechazaJsonUuidYParticipantesAusentesSinEscrituras(String cuerpo) throws Exception {
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").isString());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void rechazaTodasLasCombinacionesDeIdsRepetidos(int caso) throws Exception {
        var repetidos = switch (caso) {
            case 0 -> new ExplosionRequest(creeper.getId(), creeper.getId(), cerdo.getId());
            case 1 -> new ExplosionRequest(creeper.getId(), jugador.getId(), creeper.getId());
            default -> new ExplosionRequest(creeper.getId(), jugador.getId(), jugador.getId());
        };
        enviar(repetidos, 400, "Las tres entidades deben tener identificadores distintos");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void informaCualquieraDeLosParticipantesInexistentes(int participante) throws Exception {
        UUID id = UUID.randomUUID();
        var solicitud = sustituir(participante, id);
        enviar(solicitud, 404, "No existe una entidad con id " + id);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void rechazaTiposIncorrectosConLosMensajesDelEndpointOriginal(int participante) throws Exception {
        String tipo = List.of("Creeper", "Jugador", "Cerdo").get(participante);
        enviar(sustituir(participante, zombie.getId()), 400,
                "La entidad " + zombie.getId() + " debe ser de tipo " + tipo);
    }

    @Test
    void rechazaUnCreeperMuertoCon409SinAlterarNingunRegistro() throws Exception {
        for (int intento = 0; intento < 3; intento++) {
            enviar(new ExplosionRequest(muerto.getId(), jugador.getId(), cerdo.getId()),
                    409, "Un creeper muerto no puede explotar");
        }
    }

    private ExplosionRequest solicitud() {
        return new ExplosionRequest(creeper.getId(), jugador.getId(), cerdo.getId());
    }

    private ExplosionRequest sustituir(int participante, UUID id) {
        return new ExplosionRequest(participante == 0 ? id : creeper.getId(),
                participante == 1 ? id : jugador.getId(), participante == 2 ? id : cerdo.getId());
    }

    private String json(ExplosionRequest solicitud) throws Exception {
        return objectMapper.writeValueAsString(solicitud);
    }

    private void enviar(ExplosionRequest solicitud, int codigo, String error) throws Exception {
        var resultado = mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(json(solicitud)))
                .andExpect(status().is(codigo));
        if (error != null) {
            resultado.andExpect(jsonPath("$.error").value(error));
            assertEquals(1, objectMapper.readTree(resultado.andReturn().getResponse().getContentAsString()).size());
        } else {
            resultado.andExpect(jsonPath("$.aceptada").value(true));
        }
    }

    private void capturarReferencia() {
        estadoAnterior = estadoBase();
        estadisticas.clear();
    }

    private Map<String, List<List<String>>> estadoBase() {
        Map<String, List<List<String>>> tablas = new LinkedHashMap<>();
        for (String consulta : List.of("select * from entidades order by id",
                "select * from inventario_items order by entidad_id, posicion", "select * from demoliciones order by id")) {
            tablas.put(consulta, jdbc.query(consulta, (rs, fila) -> {
                List<String> columnas = new ArrayList<>();
                for (int indice = 1; indice <= rs.getMetaData().getColumnCount(); indice++) {
                    Object valor = rs.getObject(indice);
                    columnas.add(valor instanceof Clob clob ? clob.getSubString(1, (int) clob.length())
                            : valor == null ? null : valor.toString());
                }
                return columnas;
            }));
        }
        return tablas;
    }
}
