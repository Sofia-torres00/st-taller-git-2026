package py.edu.uc.lp3.minecraft;

import java.util.Objects;

public final class Jugador extends SerVivo {
    private final String nombreJugador;
    private final Inventario inventario;
    private int nivelExperiencia;
    private final boolean modoCreativo;

    public Jugador(String nombreJugador, Vector3D ubicacion, int capacidadInventario,
            boolean modoCreativo) {
        super(nombreJugador, TipoEntidad.JUGADOR, 20, ubicacion, 20, 0.5);
        this.nombreJugador = nombreJugador;
        this.inventario = new Inventario(capacidadInventario);
        this.modoCreativo = modoCreativo;
    }

    public void atacar(Entidad objetivo) {
        Objects.requireNonNull(objetivo, "El objetivo no puede ser nulo");
        if (objetivo == this) {
            throw new IllegalArgumentException("El jugador no puede atacarse a sí mismo");
        }
        objetivo.tomarDanio(modoCreativo ? 20 : 2);
    }

    public boolean recoger(Item item) {
        return inventario.agregar(item);
    }

    public boolean colocarBloque(Item bloque, Vector3D posicion) {
        Objects.requireNonNull(posicion, "La posición no puede ser nula");
        return modoCreativo || inventario.quitar(bloque);
    }

    public Inventario abrirInventario() {
        return inventario;
    }

    public void ganarExperiencia(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La experiencia no puede ser negativa");
        }
        nivelExperiencia += cantidad;
    }

    @Override
    public void tick() {
        actualizarEstadoVital();
    }

    @Override
    public String describirComportamiento() {
        return "El jugador explora, recoge objetos y construye.";
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public int getNivelExperiencia() {
        return nivelExperiencia;
    }

    public boolean isModoCreativo() {
        return modoCreativo;
    }
}
