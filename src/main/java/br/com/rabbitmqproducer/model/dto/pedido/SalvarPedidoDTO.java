package br.com.rabbitmqproducer.model.dto.pedido;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Dados para registrar um pedido")
public record SalvarPedidoDTO(

        @Schema(description = "Nome do cliente", example = "Maria Souza")
        @NotBlank(message = "Informe o nome do cliente!")
        @Size(max = 100, message = "Use no máximo {max} caracteres no nome do cliente!")
        String cliente,

        @Schema(description = "E-mail do cliente", example = "maria.souza@email.com")
        @NotBlank(message = "Informe o e-mail do cliente!")
        @Email(message = "Informe um e-mail válido!")
        String email,

        @Schema(description = "Valor total do pedido", example = "249.90")
        @NotNull(message = "Informe o valor do pedido!")
        @Positive(message = "O valor do pedido deve ser maior que zero!")
        BigDecimal valor

) {}