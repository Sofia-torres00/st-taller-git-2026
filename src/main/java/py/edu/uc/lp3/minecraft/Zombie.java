package py.edu.uc.lp3.minecraft;

public final class Zombie extends MobHostil {
    private final double velocidad;
    private boolean quemandose;

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

    public double getVelocidad() {
        return velocidad;
    }

    public boolean isQuemandose() {
        return quemandose;
    }
}
