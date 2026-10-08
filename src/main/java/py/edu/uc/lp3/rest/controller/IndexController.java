package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("/")
    public String inicio() {
        return "API REST - Sofia Torres - Minecraft";
    }
}