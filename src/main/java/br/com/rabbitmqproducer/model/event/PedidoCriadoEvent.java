package br.com.rabbitmqproducer.model.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PedidoCriadoEvent(

        UUID id,
        String cliente,
        String email,
        BigDecimal valor,
        LocalDateTime dataCriacao

) {}