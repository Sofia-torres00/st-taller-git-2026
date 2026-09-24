package py.edu.uc.lp3.minecraft;

public final class Cerdo extends Animal {
    private final boolean montable;

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

    public boolean isMontable() {
        return montable;
    }
}
