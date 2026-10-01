package br.com.rabbitmqproducer.exceptions.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Erro de validação de um campo da requisição")
public record MethodArgumentNotValidResponseDTO(

        @Schema(description = "Nome do campo no DTO de entrada", example = "cliente")
        String campo,

        @Schema(description = "Mensagem para a pessoa", example = "Informe o nome do cliente!")
        String mensagem

) {}