package py.edu.uc.lp3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.reactive.function.client.WebClient;

import py.edu.uc.lp3.config.RestfulApiConfig;
import py.edu.uc.lp3.rest.controller.ObjetoController;
import py.edu.uc.lp3.service.ObjetoService;

class ObjetoServiceRestTests {
    private HttpServer server;
    private MockMvc mockMvc;
    private String metodo;
    private String accept;

    @BeforeEach
    void preparar() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.start();
        var client = new RestfulApiConfig().restfulApiWebClient(WebClient.builder(),
                "http://127.0.0.1:" + server.getAddress().getPort());
        var service = new ObjetoService(client, Duration.ofSeconds(2));
        mockMvc = MockMvcBuilders.standaloneSetup(new ObjetoController(service)).build();
    }

    @AfterEach
    void detener() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void consultaYDevuelveObjetosConDatosVariablesYNulos() throws Exception {
        responder(200, """
                [
                  {"id":"1","name":"Telefono","data":{"color":"Blue","capacity GB":512,"price":389.99}},
                  {"id":"2","name":"Otro telefono","data":null}
                ]
                """);

        var resultado = mockMvc.perform(get("/api/objetos"))
                .andExpect(request().asyncStarted()).andReturn();
        mockMvc.perform(asyncDispatch(resultado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Telefono"))
                .andExpect(jsonPath("$[0].data.color").value("Blue"))
                .andExpect(jsonPath("$[0].data['capacity GB']").value(512))
                .andExpect(jsonPath("$[0].data.price").value(389.99))
                .andExpect(jsonPath("$[1].data").value(org.hamcrest.Matchers.nullValue()));
        assertEquals("GET", metodo);
        assertEquals("application/json", accept);
    }

    @Test
    void devuelveUnaListaVaciaCuandoNoHayObjetos() throws Exception {
        responder(200, "[]");

        var resultado = mockMvc.perform(get("/api/objetos")).andReturn();
        mockMvc.perform(asyncDispatch(resultado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {404, 503})
    void convierteErroresHttpExternosEnBadGateway(int codigo) throws Exception {
        responder(codigo, "{\"error\":\"Servicio no disponible\"}");

        var resultado = mockMvc.perform(get("/api/objetos")).andReturn();
        mockMvc.perform(asyncDispatch(resultado)).andExpect(status().isBadGateway());
    }

    @Test
    void devuelveBadGatewaySiNoPuedeConectarse() throws Exception {
        server.stop(0);

        var resultado = mockMvc.perform(get("/api/objetos")).andReturn();
        mockMvc.perform(asyncDispatch(resultado)).andExpect(status().isBadGateway());
    }

    @Test
    void devuelveGatewayTimeoutSiLaApiNoRespondeATiempo() throws Exception {
        server.createContext("/objects", exchange -> {
            try (exchange) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        var resultado = mockMvc.perform(get("/api/objetos")).andReturn();
        resultado.getAsyncResult(5000);
        mockMvc.perform(asyncDispatch(resultado)).andExpect(status().isGatewayTimeout());
    }

    private void responder(int codigo, String cuerpo) {
        server.createContext("/objects", exchange -> {
            try (exchange) {
                metodo = exchange.getRequestMethod();
                accept = exchange.getRequestHeaders().getFirst("Accept");
                byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(codigo, bytes.length);
                exchange.getResponseBody().write(bytes);
            }
        });
    }
}
