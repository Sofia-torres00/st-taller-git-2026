package py.edu.uc.lp3.minecraft;

import java.util.Objects;

public final class Vector3D {
    private final double x;
    private final double y;
    private final double z;

    public Vector3D(double x, double y, double z) {
        this.x = validarCoordenada(x, "x");
        this.y = validarCoordenada(y, "y");
        this.z = validarCoordenada(z, "z");
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vector3D sumar(Vector3D desplazamiento) {
        Objects.requireNonNull(desplazamiento, "El desplazamiento no puede ser nulo");
        return new Vector3D(x + desplazamiento.x, y + desplazamiento.y, z + desplazamiento.z);
    }

    public Vector3D acercarseA(Vector3D destino, double distancia) {
        Objects.requireNonNull(destino, "El destino no puede ser nulo");
        if (!Double.isFinite(distancia) || distancia < 0) {
            throw new IllegalArgumentException("La distancia debe ser finita y no negativa");
        }

        double dx = destino.x - x;
        double dy = destino.y - y;
        double dz = destino.z - z;
        double longitud = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (longitud == 0 || distancia >= longitud) {
            return destino;
        }
        double proporcion = distancia / longitud;
        return new Vector3D(x + dx * proporcion, y + dy * proporcion, z + dz * proporcion);
    }

    private static double validarCoordenada(double valor, String nombre) {
        if (!Double.isFinite(valor)) {
            throw new IllegalArgumentException("La coordenada " + nombre + " debe ser finita");
        }
        return valor;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (objeto == null || objeto.getClass() != Vector3D.class) {
            return false;
        }
        Vector3D otro = (Vector3D) objeto;
        return Double.compare(x, otro.x) == 0
                && Double.compare(y, otro.y) == 0
                && Double.compare(z, otro.z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "Vector3D{x=" + x + ", y=" + y + ", z=" + z + '}';
    }
}
