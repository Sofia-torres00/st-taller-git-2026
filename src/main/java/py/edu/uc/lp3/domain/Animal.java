package py.edu.uc.lp3.domain;

import java.util.Objects;

import jakarta.persistence.Entity;

@Entity
public abstract class Animal extends Entidad {
    private boolean domesticable;
    private int edad;
    private boolean adulto;

    protected Animal() {
    }

    protected void actualizarEdad(int edad) {
        if (edad < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa");
        }
        this.edad = edad;
        this.adulto = edad >= 20;
    }

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

    public boolean esDomesticable() {
        return domesticable;
    }

    protected void envejecer() {
        if (edad < Integer.MAX_VALUE) {
            edad++;
        }
        adulto = edad >= 20;
    }

    public int getEdad() {
        return edad;
    }

    public boolean isAdulto() {
        return adulto;
    }
}
