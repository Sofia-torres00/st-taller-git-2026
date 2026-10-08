package py.edu.uc.lp3.rest.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.TipoEntidad;
import py.edu.uc.lp3.domain.Vector3D;
import py.edu.uc.lp3.domain.Zombie;

@RestController
public class ZombieController {

    @GetMapping("/api/zombie")
    public ZombieResponse crearZombie(
            @RequestParam String nombre,
            @RequestParam double x,
            @RequestParam double y,
            @RequestParam double z,
            @RequestParam double velocidad) {
        Zombie zombie = new Zombie(nombre, new Vector3D(x, y, z), velocidad);

        return new ZombieResponse(
                zombie.getId(),
                zombie.getNombre(),
                zombie.getTipo(),
                zombie.getSalud(),
                zombie.getVelocidad(),
                zombie.estaViva(),
                zombie.obtenerPosicion(),
                zombie.describirComportamiento());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> valorInvalido(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }

    public record ZombieResponse(
            UUID id,
            String nombre,
            TipoEntidad tipo,
            double salud,
            double velocidad,
            boolean viva,
            Vector3D ubicacion,
            String comportamiento) {
    }
}
