package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Zombie")
public class Zombie extends MobHostil implements UICapable {
    private double velocidad;
    private boolean quemandose;

    protected Zombie() {
    }

    public void actualizarVelocidad(double velocidad) {
        this.velocidad = validarCantidad(velocidad, "La velocidad");
    }

    public Zombie(String nombre, Vector3D ubicacion) {
        this(nombre, ubicacion, 1.5);
    }

    public Zombie(String nombre, Vector3D ubicacion, double velocidad) {
        super(nombre, TipoEntidad.ZOMBIE, 20, ubicacion, 20, 0, 3, 35);
        this.velocidad = validarCantidad(velocidad, "La velocidad");
    }

    @Override
    public void atacarObjetivo(Entidad objetivo) {
        fijarObjetivo(objetivo);
        objetivo.tomarDanio(getFuerzaAtaque() + 1);
    }

    public void prenderFuego() {
        quemandose = true;
    }

    public void apagarFuego() {
        quemandose = false;
    }

    @Override
    public void tick() {
        super.tick();
        if (quemandose) {
            tomarDanio(1);
        }
    }

    @Override
    public String describirComportamiento() {
        return "El zombie persigue a su objetivo y ataca cuerpo a cuerpo.";
    }

    @Override
    public String emitirSonido() {
        return "Grrr...";
    }

    public double getVelocidad() {
        return velocidad;
    }

    public boolean isQuemandose() {
        return quemandose;
    }
}
