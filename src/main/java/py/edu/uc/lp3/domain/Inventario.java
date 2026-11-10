package py.edu.uc.lp3.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;

@Embeddable
public class Inventario {
    @ElementCollection
    @CollectionTable(name = "inventario_items", joinColumns = @JoinColumn(name = "entidad_id"))
    @OrderColumn(name = "posicion")
    private List<Item> items = new ArrayList<>();
    @Column(name = "capacidad_inventario")
    private int capacidad;

    protected Inventario() {
    }

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
