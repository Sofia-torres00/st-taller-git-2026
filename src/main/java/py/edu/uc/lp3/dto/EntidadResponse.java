package py.edu.uc.lp3.dto;

import java.util.UUID;
import java.util.Map;

import py.edu.uc.lp3.domain.TipoEntidad;
import py.edu.uc.lp3.domain.Vector3D;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Entidad persistida con sus atributos segun el tipo")
public record EntidadResponse(UUID id, String nombre, TipoEntidad tipo, double salud, Vector3D ubicacion,
        Map<String, Object> atributos) {
}
