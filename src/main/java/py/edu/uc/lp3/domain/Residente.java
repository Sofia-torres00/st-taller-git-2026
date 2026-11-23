package py.edu.uc.lp3.domain;

import java.util.Objects;

import jakarta.persistence.Entity;

@Entity
public abstract class Residente extends Entidad {
    private boolean puedeComerciar;

    protected Residente() {
    }

    protected Residente(String nombre, TipoEntidad tipo, double salud, Vector3D ubicacion,
            boolean puedeComerciar) {
        super(nombre, tipo, salud, ubicacion);
        this.puedeComerciar = puedeComerciar;
    }

    public void interactuar(Jugador jugador) {
        Objects.requireNonNull(jugador, "El jugador no puede ser nulo");
    }

    protected boolean puedeComerciar() {
        return puedeComerciar;
    }

    public boolean isPuedeComerciar() {
        return puedeComerciar;
    }
}
