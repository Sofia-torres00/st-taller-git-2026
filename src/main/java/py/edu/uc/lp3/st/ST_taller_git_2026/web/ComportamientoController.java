package py.edu.uc.lp3.st.ST_taller_git_2026.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.minecraft.Creeper;
import py.edu.uc.lp3.minecraft.Entidad;
import py.edu.uc.lp3.minecraft.TipoEntidad;
import py.edu.uc.lp3.minecraft.Vector3D;
import py.edu.uc.lp3.minecraft.Zombie;

@RestController
public class ComportamientoController {

    @GetMapping("/api/comportamientos")
    public ComportamientosResponse obtenerComportamientos() {
        Entidad zombie = new Zombie("Zombie1", new Vector3D(0, 64, 0), 1.5);
        Entidad creeper = new Creeper("Creeper1", new Vector3D(5, 64, 5), 3);

        List<EntidadResponse> entidades = List.of(zombie, creeper).stream()
                .map(this::crearRespuesta)
                .toList();

        return new ComportamientosResponse(entidades);
    }

    private EntidadResponse crearRespuesta(Entidad entidad) {
        return new EntidadResponse(
                entidad.getNombre(),
                entidad.getTipo(),
                entidad.describirComportamiento());
    }

    public record ComportamientosResponse(List<EntidadResponse> entidades) {
    }

    public record EntidadResponse(
            String nombre,
            TipoEntidad tipo,
            String comportamiento) {
    }
}
