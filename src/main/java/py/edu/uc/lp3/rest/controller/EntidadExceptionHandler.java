package py.edu.uc.lp3.rest.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import py.edu.uc.lp3.service.EntidadNoEncontradaException;

@RestControllerAdvice(assignableTypes = {EntidadController.class, ExplosionController.class})
public class EntidadExceptionHandler {
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> estadoInvalido(IllegalStateException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(EntidadNoEncontradaException.class)
    public ResponseEntity<Map<String, String>> noEncontrada(EntidadNoEncontradaException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> datosInvalidos(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> jsonInvalido() {
        return ResponseEntity.badRequest().body(Map.of("error", "JSON inválido: revise los campos y el tipo de entidad"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> identificadorInvalido() {
        return ResponseEntity.badRequest().body(Map.of("error", "El id debe ser un UUID válido"));
    }
}
