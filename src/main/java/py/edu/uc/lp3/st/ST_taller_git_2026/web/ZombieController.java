package py.edu.uc.lp3.st.ST_taller_git_2026.web;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.minecraft.TipoEntidad;
import py.edu.uc.lp3.minecraft.Vector3D;
import py.edu.uc.lp3.minecraft.Zombie;

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
