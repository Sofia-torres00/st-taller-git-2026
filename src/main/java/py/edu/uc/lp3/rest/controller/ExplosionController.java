package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.dto.ExplosionRequest;
import py.edu.uc.lp3.dto.ExplosionResponse;
import py.edu.uc.lp3.service.ExplosionService;

@RestController
@RequestMapping("/api/explosiones")
public class ExplosionController {
    private final ExplosionService service;

    public ExplosionController(ExplosionService service) {
        this.service = service;
    }

    @PostMapping
    public ExplosionResponse simular(@RequestBody ExplosionRequest solicitud) {
        return service.simular(solicitud);
    }
}
