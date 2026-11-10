package py.edu.uc.lp3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import py.edu.uc.lp3.domain.Cerdo;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.ExplosionInvalidaException;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Vector3D;
import py.edu.uc.lp3.domain.Zombie;

class InvariantesDominioTests {
    @Test
    void dominioRechazaNombresNoPersistiblesSinDependerDelService() {
        var posicion = new Vector3D(0, 64, 0);
        assertThrows(IllegalArgumentException.class,
                () -> new Zombie("x".repeat(101), posicion));
        var zombie = new Zombie("Anterior", posicion);
        assertThrows(IllegalArgumentException.class,
                () -> zombie.actualizarDatos("x".repeat(101), 10, posicion));
        assertEquals("Anterior", zombie.getNombre());
        assertEquals(20, zombie.getSalud());
    }

    @Test
    void envejecerNoConvierteLaEdadMaximaEnNegativa() {
        var cerdo = new Cerdo("Cerdo", new Vector3D(0, 64, 0), false, Integer.MAX_VALUE);
        cerdo.tick();
        assertEquals(Integer.MAX_VALUE, cerdo.getEdad());
        assertTrue(cerdo.isAdulto());
    }

    @Test
    void experienciaRechazaDesbordamientoYConservaSuValor() {
        var jugador = new Jugador("Alex", new Vector3D(0, 64, 0), 36, false);
        jugador.ganarExperiencia(Integer.MAX_VALUE);
        assertThrows(IllegalArgumentException.class, () -> jugador.ganarExperiencia(1));
        assertEquals(Integer.MAX_VALUE, jugador.getNivelExperiencia());
    }

    @Test
    void creeperConsumidoNoPuedeEncenderseNiTenerMechaNegativa() {
        var creeper = new Creeper("Creeper", new Vector3D(0, 64, 0), 1);
        creeper.finalizarExplosion();
        assertThrows(ExplosionInvalidaException.class, creeper::encender);
        creeper.tick();
        assertFalse(creeper.isEncendido());
        assertEquals(0, creeper.getFuseTime());

        // Restaurar salud por el CRUD no restaura la mecha consumida.
        creeper.actualizarDatos("Creeper", 20, creeper.obtenerPosicion());
        assertThrows(ExplosionInvalidaException.class, creeper::encender);
        creeper.actualizarTiempoExplosion(1);
        creeper.encender();
        creeper.tick();
        assertEquals(0, creeper.getSalud());
        assertEquals(0, creeper.getFuseTime());
        assertFalse(creeper.isEncendido());
    }

    @Test
    void muertePorVenenoDetieneLaMechaAntesDeIntentarDetonar() {
        var creeper = new Creeper("Creeper", new Vector3D(0, 64, 0), 1);
        creeper.actualizarDatos("Creeper", 1, creeper.obtenerPosicion());
        creeper.envenenar();
        creeper.encender();
        creeper.tick();
        assertEquals(0, creeper.getSalud());
        assertFalse(creeper.isEncendido());
        assertEquals(1, creeper.getFuseTime());
    }
}
