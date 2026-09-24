package py.edu.uc.lp3.minecraft;

import java.util.Objects;

public abstract class MobHostil extends SerVivo {
    private final double fuerzaAtaque;
    private final double alcanceVision;
    private Entidad objetivoActual;

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

    public final Entidad buscarObjetivo() {
        if (objetivoActual != null && !objetivoActual.estaViva()) {
            objetivoActual = null;
        }
        return objetivoActual;
    }

    protected final void fijarObjetivo(Entidad objetivo) {
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

    public final double getFuerzaAtaque() {
        return fuerzaAtaque;
    }

    public final double getAlcanceVision() {
        return alcanceVision;
    }
}
