package br.com.rabbitmqproducer.service.pedido;

import br.com.rabbitmqproducer.config.AbstractTest;
import br.com.rabbitmqproducer.model.dto.pedido.SalvarPedidoDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class PedidoServiceTest extends AbstractTest {

    @Autowired
    private PedidoService pedidoService;

    @Test
    void salvarTest() {
        var salvarPedidoDTO = new SalvarPedidoDTO(
                "João Pereira",
                "joao.pereira@email.com",
                new BigDecimal("89.90"));

        var pedido = Assertions.assertDoesNotThrow(() -> pedidoService.salvar(salvarPedidoDTO));
        Assertions.assertNotNull(pedido);
    }
}