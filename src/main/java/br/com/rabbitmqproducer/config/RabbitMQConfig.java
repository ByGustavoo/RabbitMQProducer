package br.com.rabbitmqproducer.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.amqp.autoconfigure.RabbitTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue filaPedidos(FilaPedidosProperties filaPedidosProperties) {
        return QueueBuilder.durable(filaPedidosProperties.fila()).build();
    }

    @Bean
    public DirectExchange exchangePedidos(FilaPedidosProperties filaPedidosProperties) {
        return new DirectExchange(filaPedidosProperties.exchange());
    }

    @Bean
    public Binding bindingPedidos(Queue filaPedidos, DirectExchange exchangePedidos, FilaPedidosProperties filaPedidosProperties) {
        return BindingBuilder.bind(filaPedidos)
                .to(exchangePedidos)
                .with(filaPedidosProperties.routingKey());
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplateCustomizer rabbitTemplateCustomizer() {
        return rabbitTemplate -> {
            rabbitTemplate.setConfirmCallback(RabbitMQConfig::registrarConfirmacao);
            rabbitTemplate.setReturnsCallback(RabbitMQConfig::registrarDevolucao);
        };
    }

    private static void registrarConfirmacao(CorrelationData correlationData, boolean confirmada, String motivo) {
        var id = correlationData == null ? null : correlationData.getId();

        if (confirmada) {
            log.info("Mensagem confirmada pelo RabbitMQ! - Id: {}", id);
        } else {
            log.error("Mensagem recusada pelo RabbitMQ! - Id: {} - Motivo: {}", id, motivo);
        }
    }

    private static void registrarDevolucao(ReturnedMessage returnedMessage) {
        log.warn("Mensagem devolvida sem fila de destino! - Exchange: {} - Routing key: {} - Código: {} - Motivo: {}",
                returnedMessage.getExchange(),
                returnedMessage.getRoutingKey(),
                returnedMessage.getReplyCode(),
                returnedMessage.getReplyText());
    }
}