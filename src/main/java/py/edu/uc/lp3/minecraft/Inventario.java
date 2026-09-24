package py.edu.uc.lp3.minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Inventario {
    private final List<Item> items;
    private final int capacidad;

    public Inventario(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero");
        }
        this.capacidad = capacidad;
        this.items = new ArrayList<>(capacidad);
    }

    public boolean agregar(Item item) {
        Objects.requireNonNull(item, "El item no puede ser nulo");
        if (estaLleno()) {
            return false;
        }
        return items.add(item);
    }

    public boolean quitar(Item item) {
        Objects.requireNonNull(item, "El item no puede ser nulo");
        return items.remove(item);
    }

    public boolean estaLleno() {
        return items.size() >= capacidad;
    }

    public List<Item> getItems() {
        return List.copyOf(items);
    }

    public int getCapacidad() {
        return capacidad;
    }
}
