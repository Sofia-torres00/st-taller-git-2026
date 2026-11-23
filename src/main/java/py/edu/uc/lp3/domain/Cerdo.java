package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Cerdo")
public class Cerdo extends Animal implements UICapable {
    private boolean montable;

    protected Cerdo() {
    }

    public void actualizarCaracteristicas(boolean montable, int edad) {
        actualizarEdad(edad);
        this.montable = montable;
    }

    public Cerdo(String nombre, Vector3D ubicacion, boolean montable, int edad) {
        super(nombre, TipoEntidad.CERDO, 10, ubicacion, true, edad);
        this.montable = montable;
    }

    public void alimentarse(Item item) {
        alimentar(item);
    }

    @Override
    public void tick() {
        envejecer();
    }

    @Override
    public String describirComportamiento() {
        return "El cerdo busca alimento y puede ser montado.";
    }

    @Override
    public String emitirSonido() {
        return "Oink, oink!";
    }

    public boolean isMontable() {
        return montable;
    }
}
