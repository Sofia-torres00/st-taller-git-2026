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
import py.edu.uc.lp3.persistence.repository.EntidadRepository;

@Service
public class ExplosionService {
    private final EntidadRepository repository;

    public ExplosionService(EntidadRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ExplosionResponse simular(ExplosionRequest solicitud) {
        if (solicitud == null || solicitud.creeperId() == null
                || solicitud.jugadorId() == null || solicitud.cerdoId() == null) {
            throw new IllegalArgumentException("Debe indicar creeperId, jugadorId y cerdoId");
        }
        List<UUID> ids = List.of(solicitud.creeperId(), solicitud.jugadorId(), solicitud.cerdoId());
        if (new HashSet<>(ids).size() != 3) {
            throw new IllegalArgumentException("Las tres entidades deben tener identificadores distintos");
        }

        // Solo bloquea los tres participantes, en un orden estable.
        List<Entidad> entidades = repository.buscarParaExplosion(ids);
        Creeper creeper = buscar(entidades, solicitud.creeperId(), Creeper.class);
        Jugador jugador = buscar(entidades, solicitud.jugadorId(), Jugador.class);
        Cerdo cerdo = buscar(entidades, solicitud.cerdoId(), Cerdo.class);
        creeper.validarPuedeExplotar();

        Vector3D centro = creeper.obtenerPosicion();
        List<Resultado> resultados = List.of(aplicarDanio(creeper, jugador), aplicarDanio(creeper, cerdo));
        creeper.finalizarExplosion();

        // JPA confirma los cambios de los tres participantes juntos.
        return new ExplosionResponse(creeper.getId(), centro, Creeper.RADIO_EXPLOSION, Creeper.DANIO_MAXIMO_EXPLOSION,
                creeper.getSalud(), resultados);
    }

    private <T extends Entidad> T buscar(List<Entidad> entidades, UUID id, Class<T> clase) {
        Entidad entidad = entidades.stream().filter(actual -> actual.getId().equals(id))
                .findFirst().orElseThrow(() -> new EntidadNoEncontradaException(id));
        if (!clase.isInstance(entidad)) {
            throw new IllegalArgumentException("La entidad " + id + " debe ser de tipo " + clase.getSimpleName());
        }
        return clase.cast(entidad);
    }

    private Resultado aplicarDanio(Creeper creeper, Entidad entidad) {
        double distancia = entidad.obtenerPosicion().distanciaA(creeper.obtenerPosicion());
        double saludAnterior = entidad.getSalud();
        entidad.recibirExplosion(creeper);
        return new Resultado(entidad.getId(), entidad.getNombre(), entidad.getTipo(), distancia,
                saludAnterior - entidad.getSalud(), saludAnterior, entidad.getSalud(), entidad.estaViva());
    }
}
