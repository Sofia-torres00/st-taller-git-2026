package py.edu.uc.lp3.service;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import py.edu.uc.lp3.domain.Demolicion;
import py.edu.uc.lp3.dto.DemolicionRequest;
import py.edu.uc.lp3.dto.DemolicionResponse;
import py.edu.uc.lp3.dto.ExplosionAmirResponse;
import py.edu.uc.lp3.persistence.repository.DemolicionRepository;

@Service
public class DemolicionService {
    private final NicoClient nico;
    private final AmirExplosionClient amir;
    private final DemolicionRepository repository;
    private final ObjectMapper objectMapper;

    public DemolicionService(NicoClient nico, AmirExplosionClient amir,
            DemolicionRepository repository, ObjectMapper objectMapper) {
        this.nico = nico;
        this.amir = amir;
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public DemolicionResponse ejecutar(DemolicionRequest solicitud) {
        if (solicitud == null || solicitud.explosivoNicoId() == null || solicitud.creeperAmirId() == null
                || solicitud.creeperAmirId() <= 0) {
            throw new IllegalArgumentException("Debe indicar explosivoNicoId y creeperAmirId positivo");
        }
        var disponibilidad = nico.consultar(solicitud.explosivoNicoId());
        if (!Boolean.TRUE.equals(disponibilidad.puedeExplotar())) {
            throw new ServicioRemotoException("A2", "Nico", HttpStatus.CONFLICT, "Nico indica que el explosivo no puede explotar");
        }
        // No se mantiene una transaccion H2 durante las llamadas HTTP ni se reintenta el POST de Amir.
        var resultado = amir.simular(solicitud.creeperAmirId());
        Demolicion guardada;
        try {
            guardada = repository.saveAndFlush(new Demolicion(solicitud.explosivoNicoId(), solicitud.creeperAmirId(),
                    objectMapper.writeValueAsString(resultado)));
        } catch (JsonProcessingException | DataAccessException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Amir confirmo la explosion, pero fallo el registro local. No repita la detonacion", exception);
        }
        return new DemolicionResponse(guardada.getId(), guardada.getExplosivoNicoId(), guardada.getCreeperAmirId(),
                guardada.getFecha(), resultado);
    }

    public List<DemolicionResponse> listar() {
        return repository.findAllByOrderByFechaDesc().stream().map(this::respuesta).toList();
    }

    public DemolicionResponse obtener(UUID id) {
        return respuesta(repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Demolicion no encontrada")));
    }

    private DemolicionResponse respuesta(Demolicion demolicion) {
        try {
            return new DemolicionResponse(demolicion.getId(), demolicion.getExplosivoNicoId(),
                    demolicion.getCreeperAmirId(), demolicion.getFecha(),
                    objectMapper.readValue(demolicion.getResultadoAmir(), ExplosionAmirResponse.class));
        } catch (JsonProcessingException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se puede leer el registro local", exception);
        }
    }
}
