package py.edu.uc.lp3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.reactive.function.client.WebClient;

import py.edu.uc.lp3.config.MinecraftRemotoConfig;
import py.edu.uc.lp3.domain.Cerdo;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Enderman;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.Esqueleto;
import py.edu.uc.lp3.domain.MobHostil;
import py.edu.uc.lp3.domain.Vector3D;
import py.edu.uc.lp3.domain.Zombie;
import py.edu.uc.lp3.persistence.repository.EntidadRepository;
import py.edu.uc.lp3.rest.controller.CombateIntegradoController;
import py.edu.uc.lp3.rest.controller.EntidadExceptionHandler;
import py.edu.uc.lp3.service.CombateIntegradoService;
import py.edu.uc.lp3.service.MinecraftRemotoClient;

class CombateIntegradoTests {
    private static final String RUTA = "/api/combates/integrados";
    private static final Vector3D POSICION = new Vector3D(0, 64, 0);
    private HttpServer server;
    private EntidadRepository repository;
    private MockMvc mockMvc;
    private final AtomicInteger consultasRemotas = new AtomicInteger();
    private String rutaRemota;
    private String queryRemota;
    private int codigo = 200;
    private String cuerpo = respuesta(16, true);
    private long demora;

    @BeforeEach
    void preparar() throws Exception {
        repository = mock(EntidadRepository.class);
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/entidad/", exchange -> {
            try (exchange) {
                consultasRemotas.incrementAndGet();
                rutaRemota = exchange.getRequestURI().getPath();
                queryRemota = exchange.getRequestURI().getRawQuery();
                if (demora > 0) {
                    try {
                        Thread.sleep(demora);
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                    }
                }
                byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(codigo, bytes.length);
                exchange.getResponseBody().write(bytes);
            }
        });
        server.start();
        configurarMvc(Duration.ofSeconds(5));
    }

    private void configurarMvc(Duration timeout) {
        var client = new MinecraftRemotoConfig().minecraftRemotoWebClient(WebClient.builder(),
                "http://127.0.0.1:" + server.getAddress().getPort());
        var remoto = new MinecraftRemotoClient(client, timeout);
        var service = new CombateIntegradoService(repository, remoto);
        mockMvc = MockMvcBuilders.standaloneSetup(new CombateIntegradoController(service))
                .setControllerAdvice(new EntidadExceptionHandler()).build();
    }

    @AfterEach
    void detener() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void combinaBonusDelZombieConMuerteYDropsRemotosSinModificarEntidadesLocales() throws Exception {
        Zombie atacante = new Zombie("Zombie local", POSICION);
        registrar(atacante);
        cuerpo = respuesta(0, false);

        mockMvc.perform(asyncDispatch(enviar(solicitud(atacante.getId(), "5"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.atacanteId").value(atacante.getId().toString()))
                .andExpect(jsonPath("$.danioPorGolpe").value(4.0))
                .andExpect(jsonPath("$.golpes").value(5))
                .andExpect(jsonPath("$.danioEnviado").value(20))
                .andExpect(jsonPath("$.objetivoRemoto.vidaFinal").value(0))
                .andExpect(jsonPath("$.objetivoRemoto.viva").value(false))
                .andExpect(jsonPath("$.objetivoRemoto.objetosSoltados[0]").value("carne_podrida"));
        assertEquals("/entidad/zombie", rutaRemota);
        assertEquals("danio=20", queryRemota);
        assertEquals(20, atacante.getSalud());
        assertNull(atacante.buscarObjetivo());
        verify(repository).findById(atacante.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void unGolpeEsElValorPredeterminadoYUnSupervivienteNoSueltaObjetos() throws Exception {
        Zombie atacante = new Zombie("Zombie local", POSICION);
        registrar(atacante);

        mockMvc.perform(asyncDispatch(enviar(solicitud(atacante.getId(), "null"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.golpes").value(1))
                .andExpect(jsonPath("$.danioEnviado").value(4))
                .andExpect(jsonPath("$.objetivoRemoto.viva").value(true))
                .andExpect(jsonPath("$.objetivoRemoto.objetosSoltados.length()").value(0));
        assertEquals("danio=4", queryRemota);
    }

    @ParameterizedTest
    @CsvSource({"ESQUELETO,true,4", "ESQUELETO,false,2", "ENDERMAN,false,7", "CREEPER,false,0"})
    void reutilizaElMismoDanioQueElAtaquePolimorficoLocal(String tipo, boolean arco, int danio) throws Exception {
        MobHostil atacante = switch (tipo) {
            case "ESQUELETO" -> new Esqueleto("Esqueleto", POSICION, arco);
            case "ENDERMAN" -> new Enderman("Enderman", POSICION, true);
            default -> new Creeper("Creeper", POSICION, 30);
        };
        registrar(atacante);
        mockMvc.perform(asyncDispatch(enviar(solicitud(atacante.getId(), "1"))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.danioEnviado").value(danio));
        assertEquals("danio=" + danio, queryRemota);
        Zombie objetivo = new Zombie("Objetivo local", POSICION);
        atacante.atacarObjetivo(objetivo);
        assertEquals(danio, 20 - objetivo.getSalud());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{", "null",
            "{\"atacanteId\":\"no-es-un-uuid\",\"tipoObjetivo\":\"ZOMBIE\"}",
            "{\"atacanteId\":\"11111111-1111-1111-1111-111111111111\",\"tipoObjetivo\":\"INVALIDO\"}"})
    void rechazaJsonInvalidoSinConsultarAlRemoto(String json) throws Exception {
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(repository);
        assertEquals(0, consultasRemotas.get());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 101})
    void validaLaCantidadDeGolpes(int golpes) throws Exception {
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON)
                .content(solicitud(UUID.randomUUID(), Integer.toString(golpes))))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(repository);
        assertEquals(0, consultasRemotas.get());
    }

    @Test
    void rechazaAtacanteInexistente() throws Exception {
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON)
                .content(solicitud(UUID.randomUUID(), "1"))).andExpect(status().isNotFound());
        assertEquals(0, consultasRemotas.get());
    }

    @Test
    void rechazaEntidadesQueNoSonHostiles() throws Exception {
        Cerdo cerdo = new Cerdo("Cerdo", POSICION, false, 0);
        registrar(cerdo);
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(solicitud(cerdo.getId(), "1")))
                .andExpect(status().isBadRequest());
        assertEquals(0, consultasRemotas.get());
    }

    @Test
    void unAtacanteMuertoNoPuedeCombatir() throws Exception {
        Zombie atacante = new Zombie("Zombie muerto", POSICION);
        atacante.tomarDanio(20);
        registrar(atacante);
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(solicitud(atacante.getId(), "1")))
                .andExpect(status().isConflict());
        assertEquals(0, consultasRemotas.get());
    }

    @ParameterizedTest
    @ValueSource(ints = {404, 500})
    void traduceErroresHttpDelRemotoEn502(int estado) throws Exception {
        codigo = estado;
        Zombie atacante = new Zombie("Zombie", POSICION);
        registrar(atacante);
        mockMvc.perform(asyncDispatch(enviar(solicitud(atacante.getId(), "1"))))
                .andExpect(status().isBadGateway());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "[]", "{", "", "{\"entidad\":null,\"vidaFinal\":0,\"viva\":false}"})
    void rechazaRespuestasRemotasIncompletasOVacias(String respuesta) throws Exception {
        cuerpo = respuesta;
        Zombie atacante = new Zombie("Zombie", POSICION);
        registrar(atacante);
        mockMvc.perform(asyncDispatch(enviar(solicitud(atacante.getId(), "1"))))
                .andExpect(status().isBadGateway());
    }

    @Test
    void limitaElTiempoDeEspera() throws Exception {
        configurarMvc(Duration.ofSeconds(1));
        demora = 2000;
        Zombie atacante = new Zombie("Zombie", POSICION);
        registrar(atacante);
        MvcResult resultado = enviar(solicitud(atacante.getId(), "1"));
        resultado.getAsyncResult(5000);
        mockMvc.perform(asyncDispatch(resultado)).andExpect(status().isGatewayTimeout());
    }

    @Test
    void informaSiNoPuedeConectarse() throws Exception {
        server.stop(0);
        Zombie atacante = new Zombie("Zombie", POSICION);
        registrar(atacante);
        mockMvc.perform(asyncDispatch(enviar(solicitud(atacante.getId(), "1"))))
                .andExpect(status().isBadGateway());
    }

    private void registrar(Entidad entidad) {
        when(repository.findById(entidad.getId())).thenReturn(Optional.of(entidad));
    }

    private MvcResult enviar(String json) throws Exception {
        return mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
    }

    private static String solicitud(UUID id, String golpes) {
        return "{\"atacanteId\":\"" + id + "\",\"tipoObjetivo\":\"ZOMBIE\",\"golpes\":" + golpes + "}";
    }

    private static String respuesta(int vida, boolean viva) {
        return """
                {"entidad":{"nombre":"Zombie","tipo":"ZOMBIE","drop":["carne_podrida"],"vida":%d},
                 "recibirDanio":"Resultado remoto del ataque","vidaFinal":%d,"viva":%s,
                 "comportamiento":"Zombie golpea cuerpo a cuerpo al jugador."}
                """.formatted(vida, vida, viva);
    }
}
