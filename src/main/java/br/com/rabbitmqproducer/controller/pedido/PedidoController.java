package br.com.rabbitmqproducer.controller.pedido;

import br.com.rabbitmqproducer.model.dto.pedido.PedidoDTO;
import br.com.rabbitmqproducer.model.dto.pedido.SalvarPedidoDTO;
import br.com.rabbitmqproducer.service.pedido.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/pedidos")
public class PedidoController implements PedidoDocs {

    private final PedidoService pedidoService;

    @Override
    public ResponseEntity<PedidoDTO> salvarPedido(SalvarPedidoDTO salvarPedidoDTO) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pedidoService.salvar(salvarPedidoDTO));
    }
}