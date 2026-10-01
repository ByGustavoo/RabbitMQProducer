package br.com.rabbitmqproducer.producer;

import br.com.rabbitmqproducer.config.AbstractTest;
import br.com.rabbitmqproducer.config.FilaPedidosProperties;
import br.com.rabbitmqproducer.model.event.PedidoCriadoEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
class PedidoProducerTest extends AbstractTest {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private PedidoProducer pedidoProducer;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private FilaPedidosProperties filaPedidosProperties;

    @Test
    void enviarTest() {
        var pedidoCriadoEvent = new PedidoCriadoEvent(
                UUID.randomUUID(),
                "Ana Lima",
                "ana.lima@email.com",
                new BigDecimal("1200.00"),
                LocalDateTime.now());

        amqpAdmin.purgeQueue(filaPedidosProperties.fila(), false);

        Assertions.assertDoesNotThrow(() -> pedidoProducer.enviar(pedidoCriadoEvent));

        var mensagem = rabbitTemplate.receiveAndConvert(filaPedidosProperties.fila(), 5000, new ParameterizedTypeReference<PedidoCriadoEvent>() {});
        Assertions.assertEquals(pedidoCriadoEvent, mensagem);
    }
}