package py.edu.uc.lp3.minecraft;

public final class Esqueleto extends MobHostil {
    private boolean tieneArco;

    public Esqueleto(String nombre, Vector3D ubicacion, boolean tieneArco) {
        super(nombre, TipoEntidad.ESQUELETO, 20, ubicacion, 20, 0, 2, 40);
        this.tieneArco = tieneArco;
    }

    @Override
    public void atacarObjetivo(Entidad objetivo) {
        fijarObjetivo(objetivo);
        if (tieneArco) {
            dispararFlecha();
        } else {
            objetivo.tomarDanio(getFuerzaAtaque());
        }
    }

    public void dispararFlecha() {
        Entidad objetivo = buscarObjetivo();
        if (tieneArco && objetivo != null) {
            objetivo.tomarDanio(getFuerzaAtaque() + 2);
        }
    }

    public void perderArco() {
        tieneArco = false;
    }

    @Override
    public String describirComportamiento() {
        return "El esqueleto mantiene distancia y dispara flechas.";
    }

    public boolean isTieneArco() {
        return tieneArco;
    }
}
