package br.com.rabbitmqproducer.controller.pedido;

import br.com.rabbitmqproducer.exceptions.dto.ErrorResponseDTO;
import br.com.rabbitmqproducer.model.dto.pedido.PedidoDTO;
import br.com.rabbitmqproducer.model.dto.pedido.SalvarPedidoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Pedido", description = "Endpoints que disparam eventos de pedido para o RabbitMQ")
public interface PedidoDocs {

    @PostMapping
    @Operation(
            summary = "Registra um pedido",
            description = """
                    Registra o pedido e dispara o evento PedidoCriado, que o produtor publica na \
                    exchange de pedidos para o RabbitMQ entregar na fila. O pedido não é gravado em \
                    banco: a resposta 202 indica que o evento foi aceito para processamento.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Pedido registrado e enviado para a fila!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cliente, e-mail ou valor ausente ou inválido!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "503",
                    description = "RabbitMQ indisponível!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<PedidoDTO> salvarPedido(@RequestBody @Valid SalvarPedidoDTO salvarPedidoDTO);
}