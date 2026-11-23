package py.edu.uc.lp3.service;

import org.springframework.stereotype.Service;

import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.MobHostil;
import py.edu.uc.lp3.dto.CombateIntegradoRequest;
import py.edu.uc.lp3.dto.CombateIntegradoResponse;
import py.edu.uc.lp3.persistence.repository.EntidadRepository;
import reactor.core.publisher.Mono;

@Service
public class CombateIntegradoService {
    private final EntidadRepository repository;
    private final MinecraftRemotoClient remoto;

    public CombateIntegradoService(EntidadRepository repository, MinecraftRemotoClient remoto) {
        this.repository = repository;
        this.remoto = remoto;
    }

    public Mono<CombateIntegradoResponse> simular(CombateIntegradoRequest solicitud) {
        if (solicitud == null || solicitud.atacanteId() == null || solicitud.tipoObjetivo() == null) {
            throw new IllegalArgumentException("Debe indicar atacanteId y tipoObjetivo");
        }
        int golpes = solicitud.golpes() == null ? 1 : solicitud.golpes();
        if (golpes < 1 || golpes > 100) {
            throw new IllegalArgumentException("La cantidad de golpes debe estar entre 1 y 100");
        }
        Entidad entidad = repository.findById(solicitud.atacanteId())
                .orElseThrow(() -> new EntidadNoEncontradaException(solicitud.atacanteId()));
        if (!(entidad instanceof MobHostil atacante)) {
            throw new IllegalArgumentException("El atacante debe ser un mob hostil");
        }
        if (!atacante.estaViva()) {
            throw new IllegalStateException("Un atacante muerto no puede combatir");
        }

        // Reutiliza el mismo calculo polimorfico que atacarObjetivo, incluidos sus bonus.
        double danio = atacante.calcularDanioAtaque();
        double total = danio * golpes;
        if (!Double.isFinite(total) || total < 0 || total != Math.rint(total) || total > Integer.MAX_VALUE) {
            throw new IllegalStateException("La API remota requiere un danio total entero y no negativo");
        }
        int danioEnviado = (int) total;
        // findById ya termino su transaccion: no se mantiene una transaccion JPA durante la llamada HTTP.
        return remoto.recibirDanio(solicitud.tipoObjetivo(), danioEnviado)
                .map(objetivo -> new CombateIntegradoResponse(atacante.getId(), atacante.getNombre(),
                        atacante.getTipo(), danio, golpes, danioEnviado, objetivo));
    }
}
