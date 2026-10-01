package br.com.rabbitmqproducer.model.dto.pedido;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Pedido registrado e enviado para a fila")
public record PedidoDTO(

        @Schema(description = "Id do pedido, usado também como id de correlação da mensagem")
        UUID id,

        @Schema(description = "Nome do cliente", example = "Maria Souza")
        String cliente,

        @Schema(description = "E-mail do cliente", example = "maria.souza@email.com")
        String email,

        @Schema(description = "Valor total do pedido", example = "249.90")
        BigDecimal valor,

        @Schema(description = "Data e hora do registro")
        LocalDateTime dataCriacao

) {}