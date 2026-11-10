package py.edu.uc.lp3.domain;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "entidades")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Entidad {
    @Id
    private UUID id;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEntidad tipo;
    @Column(nullable = false)
    private double salud;
    @Embedded
    private Vector3D ubicacion;

    protected Entidad() {
        // JPA restaura los campos directamente, sin setters públicos.
    }

    protected Entidad(String nombre, TipoEntidad tipo, double salud, Vector3D ubicacion) {
        this.id = UUID.randomUUID();
        this.nombre = validarNombre(nombre);
        this.tipo = Objects.requireNonNull(tipo, "El tipo no puede ser nulo");
        this.salud = validarCantidad(salud, "La salud");
        this.ubicacion = Objects.requireNonNull(ubicacion, "La ubicación no puede ser nula");
    }

    public void tomarDanio(double cantidad) {
        validarCantidad(cantidad, "El daño");
        salud = Math.max(0, salud - cantidad);
    }

    /** Regla de daño radial compartida por Jugador, Cerdo y las demás entidades. */
    public void recibirExplosion(Creeper origen) {
        Objects.requireNonNull(origen, "El origen no puede ser nulo");
        origen.validarPuedeExplotar();
        double distancia = ubicacion.distanciaA(origen.obtenerPosicion());
        if (estaViva() && distancia < Creeper.RADIO_EXPLOSION) {
            tomarDanio(Creeper.DANIO_MAXIMO_EXPLOSION * (1 - distancia / Creeper.RADIO_EXPLOSION));
        }
    }

    public void curar(double cantidad) {
        validarCantidad(cantidad, "La curación");
        if (estaViva()) {
            salud = Math.min(salud + cantidad, limiteSalud());
        }
    }

    public boolean estaViva() {
        return salud > 0;
    }

    public Vector3D obtenerPosicion() {
        return ubicacion;
    }

    public void mover(Vector3D nuevaUbicacion) {
        ubicacion = Objects.requireNonNull(nuevaUbicacion, "La ubicación no puede ser nula");
    }

    public void mover(double x, double y, double z) {
        mover(new Vector3D(x, y, z));
    }

    public void actualizarDatos(String nombre, double salud, Vector3D ubicacion) {
        String nuevoNombre = validarNombre(nombre);
        validarCantidad(salud, "La salud");
        if (salud > limiteSalud()) {
            throw new IllegalArgumentException("La salud no puede superar la salud máxima");
        }
        Objects.requireNonNull(ubicacion, "La ubicación no puede ser nula");
        this.nombre = nuevoNombre;
        this.salud = salud;
        this.ubicacion = ubicacion;
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
        String normalizado = nombre.strip();
        if (normalizado.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (normalizado.length() > 100) {
            throw new IllegalArgumentException("El nombre no puede superar los 100 caracteres");
        }
        return normalizado;
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoEntidad getTipo() {
        return tipo;
    }

    public double getSalud() {
        return salud;
    }

    public abstract void tick();

    public abstract String describirComportamiento();
}
