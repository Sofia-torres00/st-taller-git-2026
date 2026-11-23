package py.edu.uc.lp3.rest.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.dto.EntidadRequest;
import py.edu.uc.lp3.dto.EntidadResponse;
import py.edu.uc.lp3.service.EntidadService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/entidades")
@Tag(name = "Entidades", description = "CRUD de entidades persistidas en H2")
public class EntidadController {
    private final EntidadService service;

    public EntidadController(EntidadService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar entidades", description = "Devuelve todas las entidades guardadas y sus atributos.")
    @ApiResponse(responseCode = "200", description = "Lista de entidades; puede estar vacia")
    public List<EntidadResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una entidad por su ID")
    @ApiResponse(responseCode = "200", description = "Entidad encontrada")
    @ApiResponse(responseCode = "400", ref = "#/components/responses/DatosInvalidos")
    @ApiResponse(responseCode = "404", ref = "#/components/responses/EntidadNoEncontrada")
    public EntidadResponse obtener(@Parameter(description = "UUID de la entidad") @PathVariable UUID id) {
        return service.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crear una entidad", description = "Valida los datos y atributos segun el tipo y guarda la entidad.")
    @ApiResponse(responseCode = "201", description = "Entidad creada",
            headers = @Header(name = "Location", description = "URL de la nueva entidad", schema = @Schema(type = "string")))
    @ApiResponse(responseCode = "400", ref = "#/components/responses/DatosInvalidos")
    public ResponseEntity<EntidadResponse> crear(@RequestBody EntidadRequest datos) {
        EntidadResponse entidad = service.crear(datos);
        return ResponseEntity.created(URI.create("/api/entidades/" + entidad.id())).body(entidad);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una entidad",
            description = "Reemplaza nombre, salud y ubicacion; actualiza los atributos informados. El tipo no puede cambiar.")
    @ApiResponse(responseCode = "200", description = "Entidad actualizada")
    @ApiResponse(responseCode = "400", ref = "#/components/responses/DatosInvalidos")
    @ApiResponse(responseCode = "404", ref = "#/components/responses/EntidadNoEncontrada")
    public EntidadResponse actualizar(@Parameter(description = "UUID de la entidad") @PathVariable UUID id,
            @RequestBody EntidadRequest datos) {
        return service.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una entidad")
    @ApiResponse(responseCode = "204", description = "Entidad eliminada; sin cuerpo de respuesta", content = @Content)
    @ApiResponse(responseCode = "400", ref = "#/components/responses/DatosInvalidos")
    @ApiResponse(responseCode = "404", ref = "#/components/responses/EntidadNoEncontrada")
    public ResponseEntity<Void> eliminar(@Parameter(description = "UUID de la entidad") @PathVariable UUID id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
