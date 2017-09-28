package exemplo.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import exemplo.model.Cliente;
import exemplo.model.Itempedido;
import exemplo.model.Pedido;
import exemplo.model.Vendedor;
import exemplo.repository.ClienteRepository;
import exemplo.repository.ItempedidoRepository;
import exemplo.repository.PedidoRepository;
import exemplo.repository.VendedorRepository;


@Controller    
@RequestMapping(path="") 
public class PedidoController {
	@Autowired
	private PedidoRepository pedidoRepository;
	@Autowired
	private VendedorRepository vendedorRepository;
	@Autowired
	private ClienteRepository clienteRepository;


    public static final Logger logger = LoggerFactory.getLogger(PedidoController.class);
    
 
    // -------------------Retorna todos os pedidos---------------------------------------------
 
    @RequestMapping(value = "/pedido/", method = RequestMethod.GET)
    public ResponseEntity<List<Pedido>> listaTodosPedidos() {
        logger.info("Buscando todos os pedidos {}");
    	List<Pedido> pedidos = pedidoRepository.findAll();
        if (pedidos.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
            // You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<List<Pedido>>(pedidos, HttpStatus.OK);
    }
 
    // -------------------Retorna um pedido pelo ID------------------------------------------
 
    @RequestMapping(value = "/pedido/{codPedido}", method = RequestMethod.GET)
    public ResponseEntity<?> getPedidoById(@PathVariable("codPedido") Integer codPedido) {
        logger.info("Buscando pedido com codPedido {}", codPedido);
        Pedido pedido = pedidoRepository.findOne(codPedido);
        if (pedido == null) {
            logger.error("Pedido com codPedido {} nao encontrado.", codPedido);
            return new ResponseEntity(new CustomErrorType("Pedido com codPedido " + codPedido 
                    + " nao encontrado"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Pedido>(pedido, HttpStatus.OK);
    }
        
    
    // -------------------Cadastrar um Pedido-------------------------------------------
 
    @RequestMapping(value = "/pedido/", method = RequestMethod.POST)
    public ResponseEntity<?> cadastrarPedido(@RequestBody Pedido pedido, UriComponentsBuilder ucBuilder) {
        logger.info("Cadastrando Pedido : ");
        
        if (pedido.getPrazoEntrega() == null) {
            logger.error("Nao foi possivel cadastrar. É necessário um prazo de entrega.");
            return new ResponseEntity(new CustomErrorType("Nao foi possivel cadastrar. É necessário um prazo de entrega."),HttpStatus.CONFLICT);
        }
        if (pedido.getDataPedido() == null) {
            logger.error("Nao foi possivel cadastrar. É necessária a data do pedido.");
            return new ResponseEntity(new CustomErrorType("Nao foi possivel cadastrar. É necessária a data do pedido."),HttpStatus.CONFLICT);
        }        
        if (vendedorRepository.getOne(pedido.getVendedor().getCodVendedor()) == null) {
            logger.error("Nao foi possivel cadastrar. O Vendedor com o codVendedor {} nao existe", pedido.getVendedor().getCodVendedor());
            return new ResponseEntity(new CustomErrorType("Nao foi possivel cadastrar. O Vendedor com o codVendedor " + 
            		pedido.getVendedor().getCodVendedor() + " nao existe."),HttpStatus.CONFLICT);
        }
        if (clienteRepository.getOne(pedido.getCliente().getCodCliente()) == null) {
            logger.error("Nao foi possivel cadastrar. O Cliente com o codCliente {} nao existe", pedido.getCliente().getCodCliente());
            return new ResponseEntity(new CustomErrorType("Nao foi possivel cadastrar. O Cliente com o codCliente " + 
            		pedido.getCliente().getCodCliente() + " nao existe."),HttpStatus.CONFLICT);
        }
        
        Vendedor v = vendedorRepository.getOne(pedido.getVendedor().getCodVendedor());
        Cliente c = clienteRepository.getOne(pedido.getCliente().getCodCliente());
        pedido.setCliente(c);
        pedido.setVendedor(v);
        Pedido p = pedidoRepository.save(pedido);
 
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/pedido/{codPedido}").buildAndExpand(p.getCodPedido()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
 
    // ------------------- Atualizar um pedido------------------------------------------------
 
    @RequestMapping(value = "/pedido/{codPedido}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePedido(@PathVariable("codPedido") Integer codPedido, @RequestBody Pedido pedido) {
        logger.info("Atualizando o Pedido com codPedido {}", codPedido);
 
        Pedido currentPedido = pedidoRepository.findOne(codPedido);
 
        if (currentPedido == null) {
            logger.error("Nao foi possivel atualizar. Pedido com codPedido {} nao encontrado.", codPedido);
            return new ResponseEntity(new CustomErrorType("Nao foi possivel atualizar. Pedido com codPedido " + codPedido + " nao encontrado."),
                    HttpStatus.NOT_FOUND);
        }
 
        currentPedido.setCliente(pedido.getCliente());
        currentPedido.setVendedor(pedido.getVendedor());
        currentPedido.setDataPedido(pedido.getDataPedido());
        currentPedido.setPrazoEntrega(pedido.getPrazoEntrega());
        
        pedidoRepository.save(currentPedido);
        return new ResponseEntity<Pedido>(currentPedido, HttpStatus.OK);
    }
 
    // ------------------- Excluir um pedido-----------------------------------------
 
    @RequestMapping(value = "/pedido/{codPedido}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePedido(@PathVariable("codPedido") Integer codPedido) {
        logger.info("Fetching & Deleting Pedido with id {}", codPedido);
 
        Pedido pedido = pedidoRepository.findOne(codPedido);
        if (pedido == null) {
            logger.error("Nao foi possivel excluir. Pedido com codPedido {} nao encontrado.", codPedido);
            return new ResponseEntity(new CustomErrorType("Nao foi possivel excluir. Pedido com codPedido " + codPedido + " nao encontrado."),
                    HttpStatus.NOT_FOUND);
        }
        pedidoRepository.delete(codPedido);
        return new ResponseEntity<Pedido>(HttpStatus.NO_CONTENT);
    }
 
    // ------------------- Excluir todos os pedidos-----------------------------
 
    @RequestMapping(value = "/pedido/", method = RequestMethod.DELETE)
    public ResponseEntity<Pedido> deleteAllPedidos() {
        logger.info("Excluindo todos os Pedidos");
 
        pedidoRepository.deleteAll();
        return new ResponseEntity<Pedido>(HttpStatus.NO_CONTENT);
    }
 
}
