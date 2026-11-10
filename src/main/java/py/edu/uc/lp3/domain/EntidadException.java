package py.edu.uc.lp3.domain;

/** Error esperable al operar con las entidades del mundo. */
public abstract class EntidadException extends RuntimeException {
    protected EntidadException(String mensaje) {
        super(mensaje);
    }
}
