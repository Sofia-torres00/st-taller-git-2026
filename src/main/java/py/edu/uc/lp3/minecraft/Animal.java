package py.edu.uc.lp3.minecraft;

import java.util.Objects;

public abstract class Animal extends Entidad {
    private final boolean domesticable;
    private int edad;
    private boolean adulto;

    protected Animal(String nombre, TipoEntidad tipo, double salud, Vector3D ubicacion,
            boolean domesticable, int edad) {
        super(nombre, tipo, salud, ubicacion);
        if (edad < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa");
        }
        this.domesticable = domesticable;
        this.edad = edad;
        this.adulto = edad >= 20;
    }

    public void alimentar(Item item) {
        Objects.requireNonNull(item, "El alimento no puede ser nulo");
        curar(2);
    }

    public final boolean esDomesticable() {
        return domesticable;
    }

    protected final void envejecer() {
        edad++;
        adulto = edad >= 20;
    }

    public final int getEdad() {
        return edad;
    }

    public final boolean isAdulto() {
        return adulto;
    }
}
