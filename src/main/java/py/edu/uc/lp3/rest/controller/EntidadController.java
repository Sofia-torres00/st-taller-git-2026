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

@RestController
@RequestMapping("/api/entidades")
public class EntidadController {
    private final EntidadService service;

    public EntidadController(EntidadService service) {
        this.service = service;
    }

    @GetMapping
    public List<EntidadResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public EntidadResponse obtener(@PathVariable UUID id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<EntidadResponse> crear(@RequestBody EntidadRequest datos) {
        EntidadResponse entidad = service.crear(datos);
        return ResponseEntity.created(URI.create("/api/entidades/" + entidad.id())).body(entidad);
    }

    @PutMapping("/{id}")
    public EntidadResponse actualizar(@PathVariable UUID id, @RequestBody EntidadRequest datos) {
        return service.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
