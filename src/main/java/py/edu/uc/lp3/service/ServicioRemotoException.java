package py.edu.uc.lp3.service;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class ServicioRemotoException extends ResponseStatusException {
    private final String fuente;
    private final String servicio;

    public ServicioRemotoException(String fuente, String servicio, HttpStatusCode status, String mensaje) {
        this(fuente, servicio, status, mensaje, null);
    }

    public ServicioRemotoException(String fuente, String servicio, HttpStatusCode status, String mensaje, Throwable causa) {
        super(status, mensaje, causa);
        this.fuente = fuente;
        this.servicio = servicio;
    }

    public String getFuente() { return fuente; }
    public String getServicio() { return servicio; }
}
