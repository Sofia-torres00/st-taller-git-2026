package py.edu.uc.lp3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import py.edu.uc.lp3.domain.Vector3D;
import py.edu.uc.lp3.domain.Zombie;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void inicioSigueDisponible() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("API REST - Sofia Torres - Minecraft"));
    }

    @Test
    void construyeZombieConParametrosValidos() throws Exception {
        mockMvc.perform(get("/api/zombie")
                .param("nombre", "Zombie1").param("x", "0").param("y", "64")
                .param("z", "0").param("velocidad", "1.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Zombie1"))
                .andExpect(jsonPath("$.velocidad").value(1.5));
    }

    @Test
    void velocidadNegativaDevuelveBadRequestConMensajeDelDominio() throws Exception {
        mockMvc.perform(get("/api/zombie")
                .param("nombre", "Zombie1").param("x", "0").param("y", "64")
                .param("z", "0").param("velocidad", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La velocidad debe ser finita y no negativa"));
    }

    @Test
    void devuelveComportamientosDeLasDosHijas() throws Exception {
        mockMvc.perform(get("/api/comportamientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entidades.length()").value(2))
                .andExpect(jsonPath("$.entidades[0].tipo").value("ZOMBIE"))
                .andExpect(jsonPath("$.entidades[0].comportamiento")
                        .value("El zombie persigue a su objetivo y ataca cuerpo a cuerpo."))
                .andExpect(jsonPath("$.entidades[1].tipo").value("CREEPER"))
                .andExpect(jsonPath("$.entidades[1].comportamiento")
                        .value("El creeper se acerca silenciosamente y explota."));
    }

    @Test
    void constructoresYMovimientoMantienenObjetosValidos() {
        Zombie simple = new Zombie("Simple", new Vector3D(0, 64, 0));
        Zombie completo = new Zombie("Completo", new Vector3D(0, 64, 0), 2);
        assertTrue(simple.estaViva());
        assertTrue(completo.estaViva());
        assertEquals(1.5, simple.getVelocidad());
        assertEquals(2, completo.getVelocidad());

        simple.mover(1, 65, 2);
        completo.mover(new Vector3D(1, 65, 2));
        assertEquals(completo.obtenerPosicion(), simple.obtenerPosicion());
        assertThrows(IllegalArgumentException.class, () -> simple.mover(Double.NaN, 64, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new Zombie("Invalido", new Vector3D(0, 64, 0), -1));
    }

}
