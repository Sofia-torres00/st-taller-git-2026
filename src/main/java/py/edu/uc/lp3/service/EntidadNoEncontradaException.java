package py.edu.uc.lp3.service;

import java.util.UUID;

import py.edu.uc.lp3.domain.EntidadException;

public class EntidadNoEncontradaException extends EntidadException {
    public EntidadNoEncontradaException(UUID id) {
        super("No existe una entidad con id " + id);
    }
}
