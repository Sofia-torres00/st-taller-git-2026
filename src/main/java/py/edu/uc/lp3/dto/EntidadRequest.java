package py.edu.uc.lp3.dto;

import py.edu.uc.lp3.domain.TipoEntidad;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos para crear o actualizar una entidad. Los atributos permitidos dependen del tipo.",
        example = """
                {"nombre":"Zombie1","tipo":"ZOMBIE","salud":20,
                 "ubicacion":{"x":0,"y":64,"z":0},"atributos":{"velocidad":1.5}}
                """)
public record EntidadRequest(
        @Schema(description = "Nombre no vacio; se recortan los espacios de los extremos", maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED) String nombre,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) TipoEntidad tipo,
        @Schema(description = "Salud finita y no negativa", minimum = "0",
                requiredMode = Schema.RequiredMode.REQUIRED) Double salud,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Ubicacion ubicacion,
        @Schema(description = "Opcionales. Al actualizar, se conservan los atributos que no se informan.") Atributos atributos) {
    public EntidadRequest(String nombre, TipoEntidad tipo, Double salud, Ubicacion ubicacion) {
        this(nombre, tipo, salud, ubicacion, null);
    }

    @Schema(description = "Coordenadas finitas de la entidad")
    public record Ubicacion(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "0") Double x,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "64") Double y,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "0") Double z) {
    }

    @Schema(description = "ZOMBIE: velocidad; CREEPER: fuseTime; ESQUELETO: tieneArco; "
            + "ENDERMAN: puedeTomarBloques; CERDO: montable y edad; ALDEANO: profesion y nivelComercio; "
            + "JUGADOR: capacidadInventario y modoCreativo. OTRO no admite atributos especificos. "
            + "La capacidad de un inventario existente no puede cambiar.")
    public record Atributos(Double velocidad, Integer fuseTime, Boolean tieneArco,
            Boolean puedeTomarBloques, Boolean montable, Integer edad, String profesion,
            Integer nivelComercio, Integer capacidadInventario, Boolean modoCreativo) {
        public Atributos() {
            this(null, null, null, null, null, null, null, null, null, null);
        }
    }
}
