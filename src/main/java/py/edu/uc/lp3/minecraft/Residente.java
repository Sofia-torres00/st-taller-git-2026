package py.edu.uc.lp3.minecraft;

import java.util.Objects;

public abstract class Residente extends Entidad {
    private final boolean puedeComerciar;

    protected Residente(String nombre, TipoEntidad tipo, double salud, Vector3D ubicacion,
            boolean puedeComerciar) {
        super(nombre, tipo, salud, ubicacion);
        this.puedeComerciar = puedeComerciar;
    }

    public void interactuar(Jugador jugador) {
        Objects.requireNonNull(jugador, "El jugador no puede ser nulo");
    }

    protected final boolean puedeComerciar() {
        return puedeComerciar;
    }

    public final boolean isPuedeComerciar() {
        return puedeComerciar;
    }
}
