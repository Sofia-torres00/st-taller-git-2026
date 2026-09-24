package py.edu.uc.lp3.minecraft;

import java.util.Objects;

public final class Item {
    private final String id;
    private final String nombre;

    public Item(String id, String nombre) {
        this.id = validarTexto(id, "id");
        this.nombre = validarTexto(nombre, "nombre");
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    private static String validarTexto(String valor, String campo) {
        Objects.requireNonNull(valor, "El " + campo + " no puede ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException("El " + campo + " no puede estar vacío");
        }
        return valor;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (objeto == null || objeto.getClass() != Item.class) {
            return false;
        }
        Item otro = (Item) objeto;
        return id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
