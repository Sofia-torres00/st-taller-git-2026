package py.edu.uc.lp3.dto;

import py.edu.uc.lp3.domain.TipoEntidad;

public record EntidadRequest(String nombre, TipoEntidad tipo, Double salud, Ubicacion ubicacion,
        Atributos atributos) {
    public EntidadRequest(String nombre, TipoEntidad tipo, Double salud, Ubicacion ubicacion) {
        this(nombre, tipo, salud, ubicacion, null);
    }

    public record Ubicacion(Double x, Double y, Double z) {
    }

    public record Atributos(Double velocidad, Integer fuseTime, Boolean tieneArco,
            Boolean puedeTomarBloques, Boolean montable, Integer edad, String profesion,
            Integer nivelComercio, Integer capacidadInventario, Boolean modoCreativo) {
        public Atributos() {
            this(null, null, null, null, null, null, null, null, null, null);
        }
    }
}
