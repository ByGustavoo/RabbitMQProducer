package br.com.rabbitmqproducer.producer;

import br.com.rabbitmqproducer.config.FilaPedidosProperties;
import br.com.rabbitmqproducer.model.event.PedidoCriadoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class PedidoProducer {

    private final RabbitTemplate rabbitTemplate;
    private final FilaPedidosProperties filaPedidosProperties;

    @EventListener
    public void enviar(PedidoCriadoEvent pedidoCriadoEvent) {
        log.info("Enviando o pedido para a fila... - Id: {} - Exchange: {} - Routing key: {}",
                pedidoCriadoEvent.id(),
                filaPedidosProperties.exchange(),
                filaPedidosProperties.routingKey());

        rabbitTemplate.convertAndSend(
                filaPedidosProperties.exchange(),
                filaPedidosProperties.routingKey(),
                pedidoCriadoEvent,
                new CorrelationData(pedidoCriadoEvent.id().toString()));

        log.info("Pedido publicado na exchange! Aguardando a confirmação do RabbitMQ... - Id: {}", pedidoCriadoEvent.id());
    }
}