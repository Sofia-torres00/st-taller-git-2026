package py.edu.uc.lp3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import py.edu.uc.lp3.dto.DemolicionRequest;
import py.edu.uc.lp3.dto.ExplosionAmirResponse;
import py.edu.uc.lp3.dto.ExplosivoNicoResponse;
import py.edu.uc.lp3.persistence.repository.DemolicionRepository;
import py.edu.uc.lp3.service.AmirExplosionClient;
import py.edu.uc.lp3.service.DemolicionService;
import py.edu.uc.lp3.service.NicoClient;

class DemolicionPersistenceFailureTests {
    @Test
    void informaQueAmirConfirmoAunqueFalleH2YNoRepiteLaDetonacion() {
        UUID idNico = UUID.randomUUID();
        var nico = mock(NicoClient.class);
        var amir = mock(AmirExplosionClient.class);
        var repository = mock(DemolicionRepository.class);
        when(nico.consultar(idNico)).thenReturn(new ExplosivoNicoResponse(idNico, "Creeper", "Explosivo", true));
        when(amir.simular(25L)).thenReturn(new ExplosionAmirResponse(3.0,
                new ExplosionAmirResponse.EstadoCreeper(25L, false, 0, true, "Explosion aplicada"), List.of()));
        when(repository.saveAndFlush(any())).thenThrow(new DataAccessResourceFailureException("H2 no disponible"));
        var service = new DemolicionService(nico, amir, repository, new ObjectMapper());

        var exception = assertThrows(ResponseStatusException.class,
                () -> service.ejecutar(new DemolicionRequest(idNico, 25L)));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatusCode());
        assertEquals("Amir confirmo la explosion, pero fallo el registro local. No repita la detonacion", exception.getReason());
        verify(amir).simular(25L);
        verify(repository).saveAndFlush(any());
    }
}
