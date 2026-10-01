package br.com.rabbitmqproducer.service.pedido;

import br.com.rabbitmqproducer.model.dto.pedido.PedidoDTO;
import br.com.rabbitmqproducer.model.dto.pedido.SalvarPedidoDTO;
import br.com.rabbitmqproducer.model.event.PedidoCriadoEvent;
import br.com.rabbitmqproducer.model.mapper.PedidoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoMapper pedidoMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    public PedidoDTO salvar(SalvarPedidoDTO salvarPedidoDTO) {
        var pedidoCriadoEvent = new PedidoCriadoEvent(
                UUID.randomUUID(),
                salvarPedidoDTO.cliente(),
                salvarPedidoDTO.email(),
                salvarPedidoDTO.valor(),
                LocalDateTime.now());

        log.info("Pedido registrado! Disparando o evento PedidoCriado... - Id: {} - Cliente: {} - Valor: {}",
                pedidoCriadoEvent.id(),
                pedidoCriadoEvent.cliente(),
                pedidoCriadoEvent.valor());

        applicationEventPublisher.publishEvent(pedidoCriadoEvent);

        return pedidoMapper.toDTO(pedidoCriadoEvent);
    }
}