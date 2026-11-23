package py.edu.uc.lp3.domain;

import jakarta.persistence.Entity;

@Entity
public abstract class SerVivo extends Entidad {
    private double saludMaxima;
    private boolean envenenado;
    private double regeneracion;

    protected SerVivo() {
    }

    protected SerVivo(String nombre, TipoEntidad tipo, double salud, Vector3D ubicacion,
            double saludMaxima, double regeneracion) {
        super(nombre, tipo, validarSaludInicial(salud, saludMaxima), ubicacion);
        this.saludMaxima = saludMaxima;
        this.regeneracion = validarCantidad(regeneracion, "La regeneración");
    }

    public void envenenar() {
        envenenado = true;
    }

    public void curarVeneno() {
        envenenado = false;
    }

    protected void actualizarEstadoVital() {
        if (envenenado) {
            tomarDanio(1);
        } else {
            curar(regeneracion);
        }
    }

    @Override
    protected double limiteSalud() {
        return saludMaxima;
    }

    public double getSaludMaxima() {
        return saludMaxima;
    }

    public boolean isEnvenenado() {
        return envenenado;
    }

    public double getRegeneracion() {
        return regeneracion;
    }

    private static double validarSaludInicial(double salud, double saludMaxima) {
        validarCantidad(saludMaxima, "La salud máxima");
        if (saludMaxima == 0) {
            throw new IllegalArgumentException("La salud máxima debe ser mayor que cero");
        }
        validarCantidad(salud, "La salud");
        if (salud > saludMaxima) {
            throw new IllegalArgumentException("La salud no puede superar la salud máxima");
        }
        return salud;
    }
}
