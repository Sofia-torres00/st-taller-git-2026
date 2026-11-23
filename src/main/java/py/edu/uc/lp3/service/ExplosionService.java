package py.edu.uc.lp3.service;

import java.util.List;
import java.util.UUID;
import java.util.HashSet;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Cerdo;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Vector3D;
import py.edu.uc.lp3.dto.ExplosionRequest;
import py.edu.uc.lp3.dto.ExplosionResponse;
import py.edu.uc.lp3.dto.ExplosionResponse.Resultado;
import py.edu.uc.lp3.dto.ValidacionExplosionResponse;
import py.edu.uc.lp3.persistence.repository.EntidadRepository;

@Service
public class ExplosionService {
    private static final double RADIO = 6;
    private static final double DANIO_MAXIMO = 20;

    private final EntidadRepository repository;

    public ExplosionService(EntidadRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ExplosionResponse simular(ExplosionRequest solicitud) {
        List<UUID> ids = validarIdentificadores(solicitud);
        // Solo bloquea los tres participantes, en un orden estable.
        Participantes participantes = validarParticipantes(solicitud, repository.buscarParaExplosion(ids));
        Creeper creeper = participantes.creeper();

        Vector3D centro = creeper.obtenerPosicion();
        List<Resultado> resultados = List.of(aplicarDanio(centro, participantes.jugador()),
                aplicarDanio(centro, participantes.cerdo()));
        creeper.finalizarExplosion();

        // JPA confirma los cambios de los tres participantes juntos.
        return new ExplosionResponse(creeper.getId(), centro, RADIO, DANIO_MAXIMO,
                creeper.getSalud(), resultados);
    }

    @Transactional(readOnly = true)
    public ValidacionExplosionResponse validar(ExplosionRequest solicitud) {
        List<UUID> ids = validarIdentificadores(solicitud);
        // Consulta sin bloqueo de escritura y sin invocar ninguna operacion mutadora del dominio.
        validarParticipantes(solicitud, repository.findAllById(ids));
        return new ValidacionExplosionResponse(true, solicitud.creeperId(), solicitud.jugadorId(), solicitud.cerdoId());
    }

    private List<UUID> validarIdentificadores(ExplosionRequest solicitud) {
        if (solicitud == null || solicitud.creeperId() == null
                || solicitud.jugadorId() == null || solicitud.cerdoId() == null) {
            throw new IllegalArgumentException("Debe indicar creeperId, jugadorId y cerdoId");
        }
        List<UUID> ids = List.of(solicitud.creeperId(), solicitud.jugadorId(), solicitud.cerdoId());
        if (new HashSet<>(ids).size() != 3) {
            throw new IllegalArgumentException("Las tres entidades deben tener identificadores distintos");
        }

        return ids;
    }

    private Participantes validarParticipantes(ExplosionRequest solicitud, List<Entidad> entidades) {
        Creeper creeper = buscar(entidades, solicitud.creeperId(), Creeper.class);
        Jugador jugador = buscar(entidades, solicitud.jugadorId(), Jugador.class);
        Cerdo cerdo = buscar(entidades, solicitud.cerdoId(), Cerdo.class);
        if (!creeper.estaViva()) {
            throw new IllegalStateException("Un creeper muerto no puede explotar");
        }

        return new Participantes(creeper, jugador, cerdo);
    }

    private record Participantes(Creeper creeper, Jugador jugador, Cerdo cerdo) {
    }

    private <T extends Entidad> T buscar(List<Entidad> entidades, UUID id, Class<T> clase) {
        Entidad entidad = entidades.stream().filter(actual -> actual.getId().equals(id))
                .findFirst().orElseThrow(() -> new EntidadNoEncontradaException(id));
        if (!clase.isInstance(entidad)) {
            throw new IllegalArgumentException("La entidad " + id + " debe ser de tipo " + clase.getSimpleName());
        }
        return clase.cast(entidad);
    }

    private Resultado aplicarDanio(Vector3D centro, Entidad entidad) {
        Vector3D posicion = entidad.obtenerPosicion();
        double distancia = Math.hypot(Math.hypot(centro.getX() - posicion.getX(),
                centro.getY() - posicion.getY()), centro.getZ() - posicion.getZ());
        double saludAnterior = entidad.getSalud();
        if (entidad.estaViva() && distancia < RADIO) {
            double danio = DANIO_MAXIMO * (1 - distancia / RADIO);
            entidad.tomarDanio(danio);
        }
        return new Resultado(entidad.getId(), entidad.getNombre(), entidad.getTipo(), distancia,
                saludAnterior - entidad.getSalud(), saludAnterior, entidad.getSalud(), entidad.estaViva());
    }
}
