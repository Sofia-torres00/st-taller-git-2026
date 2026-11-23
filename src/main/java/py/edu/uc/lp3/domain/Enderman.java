package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Enderman")
public class Enderman extends MobHostil {
    private boolean puedeTomarBloques;

    protected Enderman() {
    }

    public void actualizarPuedeTomarBloques(boolean puedeTomarBloques) {
        this.puedeTomarBloques = puedeTomarBloques;
    }

    public Enderman(String nombre, Vector3D ubicacion, boolean puedeTomarBloques) {
        super(nombre, TipoEntidad.ENDERMAN, 40, ubicacion, 40, 0, 7, 64);
        this.puedeTomarBloques = puedeTomarBloques;
    }

    public void teletransportarse() {
        mover(obtenerPosicion().sumar(new Vector3D(5, 0, 5)));
    }

    public void tomarBloque() {
        if (!puedeTomarBloques) {
            throw new IllegalStateException("Este enderman no puede tomar bloques");
        }
    }

    @Override
    public String describirComportamiento() {
        return "El enderman se teletransporta y puede recoger bloques.";
    }

    public boolean isPuedeTomarBloques() {
        return puedeTomarBloques;
    }
}
