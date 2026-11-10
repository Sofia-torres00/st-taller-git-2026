package py.edu.uc.lp3;

import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import py.edu.uc.lp3.domain.Cerdo;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.EntidadException;
import py.edu.uc.lp3.domain.ExplosionInvalidaException;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Vector3D;
import py.edu.uc.lp3.domain.Zombie;
import py.edu.uc.lp3.dto.ExplosionRequest;
import py.edu.uc.lp3.persistence.repository.EntidadRepository;
import py.edu.uc.lp3.service.ExplosionService;
import py.edu.uc.lp3.service.EntidadNoEncontradaException;

@SpringBootTest
@AutoConfigureMockMvc
class ExplosionServiceRestTests {
    private static final String RUTA = "/api/explosiones";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private EntidadRepository repository;
    @Autowired private ExplosionService service;
    @Autowired private PlatformTransactionManager transactionManager;

    @BeforeEach
    void limpiarBase() {
        repository.deleteAll();
    }

    @Test
    void participanCreeperJugadorYCerdoYSeGuardanSusCambios() throws Exception {
        var p = participantes(1.5, 3);
        Zombie otro = new Zombie("No seleccionado", new Vector3D(0, 64, 0));
        repository.save(otro);
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(json(p.solicitud())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saludCreeper").value(0))
                .andExpect(jsonPath("$.resultados.length()").value(2))
                .andExpect(jsonPath("$.resultados[?(@.tipo == 'JUGADOR')].danioAplicado", contains(15.0)))
                .andExpect(jsonPath("$.resultados[?(@.tipo == 'JUGADOR')].saludActual", contains(5.0)))
                .andExpect(jsonPath("$.resultados[?(@.tipo == 'CERDO')].saludActual", contains(0.0)));
        assertEquals(5, salud(p.jugador()));
        assertEquals(0, salud(p.cerdo()));
        assertEquals(20, salud(otro));
        Creeper leido = (Creeper) repository.findById(p.creeper().getId()).orElseThrow();
        assertEquals(0, leido.getSalud());
        assertEquals(0, leido.getFuseTime());
        assertFalse(leido.isEncendido());
        mockMvc.perform(get("/api/entidades/" + p.creeper().getId()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.salud").value(0))
                .andExpect(jsonPath("$.atributos.fuseTime").value(0))
                .andExpect(jsonPath("$.atributos.encendido").value(false));
        mockMvc.perform(get("/api/entidades/" + p.jugador().getId()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.salud").value(5));
        mockMvc.perform(get("/api/entidades/" + p.cerdo().getId()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.salud").value(0));
    }

    @ParameterizedTest
    @CsvSource({"0,20", "1.5,15", "3,10", "6,0", "7,0"})
    void calculaElDanioPorDistanciaYRespetaElLimiteDelRadio(double distancia, double danio) throws Exception {
        var p = participantes(distancia, 7);
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(json(p.solicitud())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultados[0].danioAplicado").value(danio));
        assertEquals(20 - danio, salud(p.jugador()), 0.000001);
        assertEquals(10, salud(p.cerdo()));
    }

    @Test
    void calculaDistanciaEnTresDimensionesYNoDaniaAUnParticipanteMuerto() throws Exception {
        var p = participantes(0, 0);
        p.jugador().mover(0, 67, 4);
        p.cerdo().tomarDanio(10);
        repository.saveAll(List.of(p.jugador(), p.cerdo()));
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(json(p.solicitud())))
                .andExpect(status().isOk()).andExpect(jsonPath("$.resultados[0].distancia").value(5))
                .andExpect(jsonPath("$.resultados[1].danioAplicado").value(0));
        assertEquals(20 - 20.0 / 6, salud(p.jugador()), 0.000001);
        assertEquals(0, salud(p.cerdo()));
    }

    @Test
    void cerdoNuncaQuedaConSaludNegativaYSeInformaElDanioReal() throws Exception {
        var p = participantes(7, 0);
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(json(p.solicitud())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultados[1].danioAplicado").value(10))
                .andExpect(jsonPath("$.resultados[1].saludActual").value(0));
        assertEquals(0, salud(p.cerdo()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{", "{\"creeperId\":\"incorrecto\"}"})
    void rechazaSolicitudesIncompletasOInvalidas(String solicitud) throws Exception {
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(solicitud))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").isString());
        assertEquals(0, repository.count());
    }

    @Test
    void validaIdentificadoresDistintosExistenciaYTiposAntesDeModificarSalud() throws Exception {
        var p = participantes(0, 0);
        var repetidos = new ExplosionRequest(p.creeper().getId(), p.jugador().getId(), p.jugador().getId());
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(json(repetidos)))
                .andExpect(status().isBadRequest());
        var inexistente = new ExplosionRequest(p.creeper().getId(), p.jugador().getId(), UUID.randomUUID());
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(json(inexistente)))
                .andExpect(status().isNotFound());
        var tiposInvertidos = new ExplosionRequest(p.creeper().getId(), p.cerdo().getId(), p.jugador().getId());
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(json(tiposInvertidos)))
                .andExpect(status().isBadRequest());
        assertEquals(20, salud(p.creeper()));
        assertEquals(20, salud(p.jugador()));
        assertEquals(10, salud(p.cerdo()));
    }

    @Test
    void rechazaSegundaExplosionSinAplicarMasDanio() throws Exception {
        var p = participantes(3, 7);
        String cuerpo = json(p.solicitud());
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isOk());
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("EXPLOSION_INVALIDA"))
                .andExpect(jsonPath("$.error").isString());
        assertEquals(10, salud(p.jugador()));
    }

    @Test
    void dominioSostieneAmbasReglasSinServiceNiHttp() {
        var creeper = new Creeper("Origen", new Vector3D(0, 64, 0), 30);
        var jugador = new Jugador("Jugador", new Vector3D(1.5, 64, 0), 36, false);
        var cerdo = new Cerdo("Cerdo", new Vector3D(3, 64, 0), false, 0);
        creeper.encender();
        jugador.recibirExplosion(creeper);
        cerdo.recibirExplosion(creeper);
        creeper.finalizarExplosion();
        assertEquals(5, jugador.getSalud());
        assertEquals(0, cerdo.getSalud());
        assertEquals(0, creeper.getSalud());
        assertEquals(0, creeper.getFuseTime());
        assertFalse(creeper.isEncendido());
        assertThrows(ExplosionInvalidaException.class, creeper::finalizarExplosion);
        assertThrows(ExplosionInvalidaException.class, () -> jugador.recibirExplosion(creeper));
        assertEquals(5, jugador.getSalud());
    }

    @Test
    void servicioLanzaHijasDeLaMismaJerarquiaAntesDeModificarDatos() {
        var p = participantes(0, 0);
        EntidadException inexistente = assertThrows(EntidadNoEncontradaException.class,
                () -> service.simular(new ExplosionRequest(p.creeper().getId(),
                        p.jugador().getId(), UUID.randomUUID())));
        assertEquals(20, salud(p.creeper()));
        service.simular(p.solicitud());
        EntidadException explosion = assertThrows(ExplosionInvalidaException.class,
                () -> service.simular(p.solicitud()));
        org.junit.jupiter.api.Assertions.assertNotEquals(inexistente.getClass(), explosion.getClass());
    }

    @Test
    void unFalloRevierteLosCambiosDeLasTresClases() {
        var p = participantes(0, 0);
        var transaccion = new TransactionTemplate(transactionManager);
        assertThrows(IllegalStateException.class, () -> transaccion.executeWithoutResult(estado -> {
            service.simular(p.solicitud());
            throw new IllegalStateException("Fallo antes de confirmar");
        }));
        assertEquals(20, salud(p.creeper()));
        assertEquals(20, salud(p.jugador()));
        assertEquals(10, salud(p.cerdo()));
    }

    @Test
    void solicitudesConcurrentesNoDuplicanElDanio() throws Exception {
        var p = participantes(3, 7);
        String cuerpo = json(p.solicitud());
        CountDownLatch inicio = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var primera = executor.submit(() -> {
                inicio.await();
                return mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                        .andReturn().getResponse().getStatus();
            });
            var segunda = executor.submit(() -> {
                inicio.await();
                return mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                        .andReturn().getResponse().getStatus();
            });
            inicio.countDown();
            var codigos = List.of(primera.get(15, TimeUnit.SECONDS), segunda.get(15, TimeUnit.SECONDS))
                    .stream().sorted().toList();
            assertEquals(List.of(200, 409), codigos);
        }
        assertEquals(10, salud(p.jugador()));
    }

    private Participantes participantes(double xJugador, double xCerdo) {
        var creeper = new Creeper("Creeper", new Vector3D(0, 64, 0), 30);
        creeper.encender();
        var jugador = new Jugador("Jugador", new Vector3D(xJugador, 64, 0), 36, false);
        var cerdo = new Cerdo("Cerdo", new Vector3D(xCerdo, 64, 0), false, 0);
        repository.saveAll(List.of(creeper, jugador, cerdo));
        return new Participantes(creeper, jugador, cerdo);
    }

    private String json(ExplosionRequest solicitud) throws Exception {
        return objectMapper.writeValueAsString(solicitud);
    }

    private double salud(Entidad entidad) {
        return repository.findById(entidad.getId()).orElseThrow().getSalud();
    }

    private record Participantes(Creeper creeper, Jugador jugador, Cerdo cerdo) {
        ExplosionRequest solicitud() {
            return new ExplosionRequest(creeper.getId(), jugador.getId(), cerdo.getId());
        }
    }
}
