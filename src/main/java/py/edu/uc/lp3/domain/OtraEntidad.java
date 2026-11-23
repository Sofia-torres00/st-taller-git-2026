package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("OtraEntidad")
public class OtraEntidad extends Entidad {
    protected OtraEntidad() {
    }

    public OtraEntidad(String nombre, Vector3D ubicacion) {
        super(nombre, TipoEntidad.OTRO, 0, ubicacion);
    }

    @Override
    public void tick() {
        // Una entidad genérica no tiene comportamiento periódico.
    }

    @Override
    public String describirComportamiento() {
        return "Entidad genérica sin comportamiento específico.";
    }
}
