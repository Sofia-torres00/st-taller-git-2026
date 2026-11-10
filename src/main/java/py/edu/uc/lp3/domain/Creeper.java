package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Creeper")
public class Creeper extends MobHostil {
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
        encendido = true;
    }

    public void acercarse() {
        Entidad objetivo = buscarObjetivo();
        if (objetivo != null) {
            mover(obtenerPosicion().acercarseA(objetivo.obtenerPosicion(), 1));
        }
    }

    public void explotar() {
        Entidad objetivo = buscarObjetivo();
        if (objetivo != null) {
            objetivo.tomarDanio(20);
        }
        finalizarExplosion();
    }

    public void finalizarExplosion() {
        tomarDanio(getSalud());
        encendido = false;
        fuseTime = 0;
    }

    @Override
    public void tick() {
        super.tick();
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
