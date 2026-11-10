package py.edu.uc.lp3.dto;

import java.util.List;
import java.util.UUID;

import py.edu.uc.lp3.domain.TipoEntidad;
import py.edu.uc.lp3.domain.Vector3D;

public record ExplosionResponse(UUID creeperId, Vector3D centro, double radio, double danioMaximo,
        double saludCreeper, List<Resultado> resultados) {
    public record Resultado(UUID id, String nombre, TipoEntidad tipo, double distancia,
            double danioAplicado, double saludAnterior, double saludActual, boolean viva) {
    }
}
