package py.edu.uc.lp3.minecraft;

import java.util.Objects;

public final class Aldeano extends Residente {
    private final String profesion;
    private int nivelComercio;

    public Aldeano(String nombre, Vector3D ubicacion, String profesion, int nivelComercio) {
        super(nombre, TipoEntidad.ALDEANO, 20, ubicacion, true);
        Objects.requireNonNull(profesion, "La profesión no puede ser nula");
        if (profesion.isBlank()) {
            throw new IllegalArgumentException("La profesión no puede estar vacía");
        }
        if (nivelComercio < 1 || nivelComercio > 5) {
            throw new IllegalArgumentException("El nivel de comercio debe estar entre 1 y 5");
        }
        this.profesion = profesion;
        this.nivelComercio = nivelComercio;
    }

    public void comerciar(Jugador jugador) {
        Objects.requireNonNull(jugador, "El jugador no puede ser nulo");
        if (!puedeComerciar()) {
            throw new IllegalStateException("El aldeano no puede comerciar");
        }
        nivelComercio = Math.min(5, nivelComercio + 1);
    }

    @Override
    public void interactuar(Jugador jugador) {
        comerciar(jugador);
    }

    @Override
    public void tick() {
        // El UML no define cambios periódicos para el aldeano.
    }

    @Override
    public String describirComportamiento() {
        return "El aldeano ofrece intercambios según su profesión.";
    }

    public String getProfesion() {
        return profesion;
    }

    public int getNivelComercio() {
        return nivelComercio;
    }
}
