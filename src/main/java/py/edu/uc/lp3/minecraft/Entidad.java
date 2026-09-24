package py.edu.uc.lp3.minecraft;

import java.util.Objects;
import java.util.UUID;

public abstract class Entidad {
    private final UUID id;
    private final String nombre;
    private final TipoEntidad tipo;
    private double salud;
    private Vector3D ubicacion;

    protected Entidad(String nombre, TipoEntidad tipo, double salud, Vector3D ubicacion) {
        this.id = UUID.randomUUID();
        this.nombre = validarNombre(nombre);
        this.tipo = Objects.requireNonNull(tipo, "El tipo no puede ser nulo");
        this.salud = validarCantidad(salud, "La salud");
        this.ubicacion = Objects.requireNonNull(ubicacion, "La ubicación no puede ser nula");
    }

    public final void tomarDanio(double cantidad) {
        validarCantidad(cantidad, "El daño");
        salud = Math.max(0, salud - cantidad);
    }

    public final void curar(double cantidad) {
        validarCantidad(cantidad, "La curación");
        if (estaViva()) {
            salud = Math.min(salud + cantidad, limiteSalud());
        }
    }

    public final boolean estaViva() {
        return salud > 0;
    }

    public final Vector3D obtenerPosicion() {
        return ubicacion;
    }

    public final void mover(Vector3D nuevaUbicacion) {
        ubicacion = Objects.requireNonNull(nuevaUbicacion, "La ubicación no puede ser nula");
    }

    protected double limiteSalud() {
        return Double.MAX_VALUE;
    }

    protected static double validarCantidad(double cantidad, String campo) {
        if (!Double.isFinite(cantidad) || cantidad < 0) {
            throw new IllegalArgumentException(campo + " debe ser finita y no negativa");
        }
        return cantidad;
    }

    private static String validarNombre(String nombre) {
        Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        if (nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        return nombre;
    }

    public final UUID getId() {
        return id;
    }

    public final String getNombre() {
        return nombre;
    }

    public final TipoEntidad getTipo() {
        return tipo;
    }

    public final double getSalud() {
        return salud;
    }

    public abstract void tick();

    public abstract String describirComportamiento();
}
