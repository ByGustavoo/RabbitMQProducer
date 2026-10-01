package br.com.rabbitmqproducer.model.mapper;

import br.com.rabbitmqproducer.model.dto.pedido.PedidoDTO;
import br.com.rabbitmqproducer.model.event.PedidoCriadoEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    PedidoDTO toDTO(PedidoCriadoEvent pedidoCriadoEvent);
}