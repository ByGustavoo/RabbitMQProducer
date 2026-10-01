package br.com.rabbitmqproducer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rabbitmq.pedidos")
public record FilaPedidosProperties(

        String fila,
        String exchange,
        String routingKey

) {}