package py.edu.uc.lp3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.domain.Aldeano;
import py.edu.uc.lp3.domain.Cerdo;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Enderman;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.Esqueleto;
import py.edu.uc.lp3.domain.Item;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.OtraEntidad;
import py.edu.uc.lp3.domain.TipoEntidad;
import py.edu.uc.lp3.domain.Vector3D;
import py.edu.uc.lp3.domain.Zombie;
import py.edu.uc.lp3.dto.EntidadRequest;
import py.edu.uc.lp3.persistence.repository.EntidadRepository;
import py.edu.uc.lp3.service.EntidadService;

@SpringBootTest
@Transactional
class EntidadJpaTests {
    @Autowired private EntityManager entityManager;
    @Autowired private EntidadRepository repository;
    @Autowired private EntidadService service;
    @Autowired private JdbcTemplate jdbc;

    @ParameterizedTest
    @EnumSource(TipoEntidad.class)
    void restauraCadaSubclaseDesdeLaTablaUnica(TipoEntidad tipo) {
        var creado = service.crear(new EntidadRequest("Personaje", tipo, 10.0,
                new EntidadRequest.Ubicacion(1.0, 64.0, 2.0)));
        entityManager.flush();
        entityManager.clear();

        Map<TipoEntidad, Class<? extends Entidad>> clases = Map.of(
                TipoEntidad.ZOMBIE, Zombie.class, TipoEntidad.CREEPER, Creeper.class,
                TipoEntidad.ESQUELETO, Esqueleto.class, TipoEntidad.ENDERMAN, Enderman.class,
                TipoEntidad.CERDO, Cerdo.class, TipoEntidad.ALDEANO, Aldeano.class,
                TipoEntidad.JUGADOR, Jugador.class, TipoEntidad.OTRO, OtraEntidad.class);
        Entidad leido = repository.findById(creado.id()).orElseThrow();
        assertInstanceOf(clases.get(tipo), leido);
        assertEquals(10, leido.getSalud());
        assertEquals(new Vector3D(1, 64, 2), leido.obtenerPosicion());
        assertEquals(clases.get(tipo).getSimpleName(),
                jdbc.queryForObject("select dtype from entidades where id = ?", String.class, creado.id()));
    }

    @Test
    void restauraEstadoHerenciaObjetivoEInventario() {
        Jugador jugador = new Jugador("Alex", new Vector3D(3, 64, 0), 10, false);
        jugador.recoger(new Item("piedra", "Piedra"));
        jugador.ganarExperiencia(7);
        entityManager.persist(jugador);
        Zombie zombie = new Zombie("Hostil", new Vector3D(0, 64, 0), 3.5);
        zombie.atacarObjetivo(jugador);
        zombie.prenderFuego();
        entityManager.persist(zombie);
        entityManager.flush();
        entityManager.clear();

        Zombie leido = (Zombie) repository.findById(zombie.getId()).orElseThrow();
        assertEquals(3.5, leido.getVelocidad());
        assertEquals(3, leido.getFuerzaAtaque());
        assertEquals(20, leido.getSaludMaxima());
        assertTrue(leido.isQuemandose());
        Jugador objetivo = (Jugador) Hibernate.unproxy(leido.buscarObjetivo());
        assertEquals(jugador.getId(), objetivo.getId());
        assertEquals(16, objetivo.getSalud());
        assertEquals(7, objetivo.getNivelExperiencia());
        assertEquals(10, objetivo.abrirInventario().getCapacidad());
        assertEquals("Piedra", objetivo.abrirInventario().getItems().getFirst().getNombre());
        leido.tick();
        assertEquals(19, leido.getSalud());
    }

    @Test
    void actualizarJugadorConservaSuInventarioYExperiencia() {
        Jugador jugador = new Jugador("Alex", new Vector3D(0, 64, 0), 10, false);
        jugador.recoger(new Item("piedra", "Piedra"));
        jugador.ganarExperiencia(7);
        entityManager.persist(jugador);
        entityManager.flush();
        entityManager.clear();

        service.actualizar(jugador.getId(), new EntidadRequest("Steve", TipoEntidad.JUGADOR,
                15.0, new EntidadRequest.Ubicacion(1.0, 65.0, 2.0)));
        entityManager.flush();
        entityManager.clear();
        Jugador leido = (Jugador) repository.findById(jugador.getId()).orElseThrow();
        assertEquals("Steve", leido.getNombreJugador());
        assertEquals(7, leido.getNivelExperiencia());
        assertEquals(1, leido.abrirInventario().getItems().size());
    }

    @Test
    void eliminarObjetivoDesvinculaLosMobs() {
        Jugador jugador = new Jugador("Alex", new Vector3D(0, 64, 0), 10, false);
        Zombie zombie = new Zombie("Hostil", new Vector3D(1, 64, 0));
        zombie.atacarObjetivo(jugador);
        entityManager.persist(jugador);
        entityManager.persist(zombie);
        entityManager.flush();
        service.eliminar(jugador.getId());
        entityManager.flush();
        entityManager.clear();
        assertTrue(repository.findById(jugador.getId()).isEmpty());
        assertNull(((Zombie) repository.findById(zombie.getId()).orElseThrow()).buscarObjetivo());
    }
}
