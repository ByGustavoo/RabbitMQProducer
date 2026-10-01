package br.com.rabbitmqproducer.controller.pedido;

import br.com.rabbitmqproducer.config.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
class PedidoControllerTest extends AbstractControllerTest {

    private String salvarPedidoRequest;

    @BeforeEach
    void setUp() throws IOException {
        if (salvarPedidoRequest == null) {
            salvarPedidoRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/requests/pedido/salvarPedidoRequest.json")));
        }
    }

    @Test
    void salvarPedidoTest() throws Exception {
        testPost("/v1/pedidos", salvarPedidoRequest);
    }
}