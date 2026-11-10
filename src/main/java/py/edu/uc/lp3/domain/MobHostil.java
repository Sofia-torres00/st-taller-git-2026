package py.edu.uc.lp3.domain;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;

@Entity
public abstract class MobHostil extends SerVivo {
    private double fuerzaAtaque;
    private double alcanceVision;
    @ManyToOne(fetch = FetchType.LAZY)
    private Entidad objetivoActual;

    protected MobHostil() {
    }

    protected MobHostil(String nombre, TipoEntidad tipo, double salud, Vector3D ubicacion,
            double saludMaxima, double regeneracion, double fuerzaAtaque, double alcanceVision) {
        super(nombre, tipo, salud, ubicacion, saludMaxima, regeneracion);
        this.fuerzaAtaque = validarCantidad(fuerzaAtaque, "La fuerza de ataque");
        this.alcanceVision = validarCantidad(alcanceVision, "El alcance de visión");
    }

    public void atacarObjetivo(Entidad objetivo) {
        fijarObjetivo(objetivo);
        objetivo.tomarDanio(fuerzaAtaque);
    }

    public Entidad buscarObjetivo() {
        if (objetivoActual != null && !objetivoActual.estaViva()) {
            objetivoActual = null;
        }
        return objetivoActual;
    }

    protected void fijarObjetivo(Entidad objetivo) {
        Objects.requireNonNull(objetivo, "El objetivo no puede ser nulo");
        if (objetivo == this) {
            throw new IllegalArgumentException("Una entidad no puede atacarse a sí misma");
        }
        objetivoActual = objetivo;
    }

    @Override
    public void tick() {
        actualizarEstadoVital();
    }

    public double getFuerzaAtaque() {
        return fuerzaAtaque;
    }

    public double getAlcanceVision() {
        return alcanceVision;
    }
}
