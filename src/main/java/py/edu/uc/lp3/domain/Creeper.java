package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Creeper")
public class Creeper extends MobHostil {
    public static final double RADIO_EXPLOSION = 6;
    public static final double DANIO_MAXIMO_EXPLOSION = 20;
    private int fuseTime;
    private boolean encendido;

    protected Creeper() {
    }

    public void actualizarTiempoExplosion(int fuseTime) {
        if (fuseTime <= 0) {
            throw new IllegalArgumentException("El tiempo de explosión debe ser mayor que cero");
        }
        this.fuseTime = fuseTime;
    }

    public Creeper(String nombre, Vector3D ubicacion, int fuseTime) {
        super(nombre, TipoEntidad.CREEPER, 20, ubicacion, 20, 0, 0, 20);
        if (fuseTime <= 0) {
            throw new IllegalArgumentException("El tiempo de explosión debe ser mayor que cero");
        }
        this.fuseTime = fuseTime;
    }

    public void encender() {
        validarPuedeExplotar();
        if (fuseTime <= 0) {
            throw new ExplosionInvalidaException("El creeper necesita una mecha activa para encenderse");
        }
        encendido = true;
    }

    public void acercarse() {
        Entidad objetivo = buscarObjetivo();
        if (objetivo != null) {
            mover(obtenerPosicion().acercarseA(objetivo.obtenerPosicion(), 1));
        }
    }

    public void explotar() {
        validarPuedeExplotar();
        Entidad objetivo = buscarObjetivo();
        if (objetivo != null) {
            objetivo.tomarDanio(20);
        }
        finalizarExplosion();
    }

    public void finalizarExplosion() {
        validarPuedeExplotar();
        tomarDanio(getSalud());
        encendido = false;
        fuseTime = 0;
    }

    public void validarPuedeExplotar() {
        if (!estaViva()) {
            throw new ExplosionInvalidaException("Un creeper muerto no puede explotar");
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!estaViva()) {
            encendido = false;
            return;
        }
        if (encendido && --fuseTime == 0) {
            explotar();
        } else if (!encendido) {
            acercarse();
        }
    }

    @Override
    public String describirComportamiento() {
        return "El creeper se acerca silenciosamente y explota.";
    }

    public int getFuseTime() {
        return fuseTime;
    }

    public boolean isEncendido() {
        return encendido;
    }
}
