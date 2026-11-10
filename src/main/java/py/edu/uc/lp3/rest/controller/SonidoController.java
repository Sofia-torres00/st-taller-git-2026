package py.edu.uc.lp3.rest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.Cerdo;
import py.edu.uc.lp3.domain.UICapable;
import py.edu.uc.lp3.domain.Vector3D;
import py.edu.uc.lp3.domain.Zombie;

@RestController
public class SonidoController {

    @GetMapping("/api/sonidos")
    public SonidosResponse obtenerSonidos() {
        UICapable zombie = new Zombie("Zombie1", new Vector3D(0, 64, 0));
        UICapable cerdo = new Cerdo("Cerdo1", new Vector3D(5, 64, 5), true, 20);

        return new SonidosResponse(List.of(
                crearRespuesta("Zombie", zombie),
                crearRespuesta("Cerdo", cerdo)));
    }

    private SonidoResponse crearRespuesta(String tipo, UICapable entidad) {
        return new SonidoResponse(tipo, entidad.emitirSonido());
    }

    public record SonidosResponse(List<SonidoResponse> resultados) {
    }

    public record SonidoResponse(String tipo, String mensaje) {
    }
}
