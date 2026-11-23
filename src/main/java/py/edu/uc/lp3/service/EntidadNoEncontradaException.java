package py.edu.uc.lp3.service;

import java.util.UUID;

public class EntidadNoEncontradaException extends RuntimeException {
    public EntidadNoEncontradaException(UUID id) {
        super("No existe una entidad con id " + id);
    }
}
