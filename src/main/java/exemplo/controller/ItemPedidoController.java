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

import exemplo.model.Itempedido;
import exemplo.model.Pedido;
import exemplo.model.Produto;
import exemplo.repository.ItempedidoRepository;
import exemplo.repository.PedidoRepository;
import exemplo.repository.ProdutoRepository;



@Controller    
@RequestMapping(path="") 
public class ItemPedidoController {
	@Autowired
	private ItempedidoRepository itempedidoRepository;
	@Autowired
	private ProdutoRepository produtoRepository;
	@Autowired
	private PedidoRepository pedidoRepository;
	
    public static final Logger logger = LoggerFactory.getLogger(ItemPedidoController.class);
    
 
    // -------------------Retorna todos os itempedidos---------------------------------------------
 
    @RequestMapping(value = "/itempedido/", method = RequestMethod.GET)
    public ResponseEntity<List<Itempedido>> listaTodosItempedidos() {
        logger.info("Buscando todos os itempedidos {}");
    	List<Itempedido> itempedidos = itempedidoRepository.findAll();
        if (itempedidos.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
            // You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<List<Itempedido>>(itempedidos, HttpStatus.OK);
    }
 
    // -------------------Retorna um itempedido pelo ID------------------------------------------
 
    @RequestMapping(value = "/itempedido/{codItempedido}", method = RequestMethod.GET)
    public ResponseEntity<?> getItempedidoById(@PathVariable("codItempedido") Integer codItempedido) {
        logger.info("Buscando itempedido com codItempedido {}", codItempedido);
        Itempedido itempedido = itempedidoRepository.findOne(codItempedido);
        if (itempedido == null) {
            logger.error("Itempedido com codItempedido {} nao encontrado.", codItempedido);
            return new ResponseEntity(new CustomErrorType("Itempedido com codItempedido " + codItempedido 
                    + " nao encontrado"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Itempedido>(itempedido, HttpStatus.OK);
    }
        
    // -------------------Retorna os itempedidos pelo codPedido------------------------------------------
    
    @RequestMapping(value = "/itempedido/codpedido/{codPedido}", method = RequestMethod.GET)
    public ResponseEntity<?> getItempedidosByCodPedido(@PathVariable("codPedido") Integer codPedido) {
        logger.info("Buscando itempedidos com codPedido {}", codPedido);
        List<Itempedido> itempedidos = itempedidoRepository.findByPedidoByCodPedido(codPedido);
        if (itempedidos.isEmpty()) {
            logger.error("Itempedidos com codPedido {} nao encontrado.", codPedido);
            return new ResponseEntity(new CustomErrorType("Itempedidos com codPedido " + codPedido 
                    + " nao encontrado"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<Itempedido>>(itempedidos, HttpStatus.OK);
    }    
    
    
    
    // -------------------Cadastrar um Itempedido-------------------------------------------
 
    @RequestMapping(value = "/itempedido/", method = RequestMethod.POST)
    public ResponseEntity<?> cadastrarItempedido(@RequestBody Itempedido itempedido, UriComponentsBuilder ucBuilder) {
        logger.info("Cadastrando Itempedido : ");
 
        if ((itempedido.getProduto() == null) || (itempedido.getProduto().getCodProduto() <= 0)){
            logger.error("Nao foi possivel cadastrar. É necessário um produto vinculado ao itempedido.");
            return new ResponseEntity(new CustomErrorType("Nao foi possivel cadastrar. É necessário um produto vinculado ao itempedido."),HttpStatus.CONFLICT);
        }
        if (itempedido.getQuantidade() <= 0){
            logger.error("Nao foi possivel cadastrar. É necessário uma quantidade maior que zero.");
            return new ResponseEntity(new CustomErrorType("Nao foi possivel cadastrar. É necessário uma quantidade maior que zero. "),HttpStatus.CONFLICT);
        }
        if (produtoRepository.getOne((itempedido.getProduto().getCodProduto())) == null) {
            logger.error("Nao foi possivel cadastrar. O produto {} relacionado a este itempedido nao existe.", itempedido.getProduto().getCodProduto());
            return new ResponseEntity(new CustomErrorType("Nao foi possivel cadastrar. O produto " + 
            		itempedido.getProduto().getCodProduto() + "  relacionado a este itempedido nao existe."),HttpStatus.CONFLICT);
        }
        if (pedidoRepository.getOne((itempedido.getPedido().getCodPedido())) == null) {
            logger.error("Nao foi possivel cadastrar. O pedido {} relacionado a este itempedido nao existe.", itempedido.getPedido().getCodPedido());
            return new ResponseEntity(new CustomErrorType("Nao foi possivel cadastrar. O produto " + 
            		itempedido.getPedido().getCodPedido() + "  relacionado a este itempedido nao existe."),HttpStatus.CONFLICT);
        }

        Pedido p = pedidoRepository.getOne(itempedido.getPedido().getCodPedido());
        Produto pr = produtoRepository.getOne(itempedido.getProduto().getCodProduto());
        itempedido.setProduto(pr);
        itempedido.setPedido(p);
        Itempedido i = itempedidoRepository.save(itempedido);
  
        
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/itempedido/{codItempedido}").buildAndExpand(i.getCodItemPedido()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
 
    // ------------------- Atualizar um itempedido------------------------------------------------
 
    @RequestMapping(value = "/itempedido/{codItempedido}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateItempedido(@PathVariable("codItempedido") Integer codItempedido, @RequestBody Itempedido itempedido) {
        logger.info("Atualizando o Itempedido com codItempedido {}", codItempedido);
 
        Itempedido currentItempedido = itempedidoRepository.findOne(codItempedido);
 
        if (currentItempedido == null) {
            logger.error("Nao foi possivel atualizar. Itempedido com codItempedido {} nao encontrado.", codItempedido);
            return new ResponseEntity(new CustomErrorType("Nao foi possivel atualizar. Itempedido com codItempedido " + codItempedido + " nao encontrado."),
                    HttpStatus.NOT_FOUND);
        }
 
        currentItempedido.setProduto(itempedido.getProduto());
        currentItempedido.setQuantidade(itempedido.getQuantidade());
 
        itempedidoRepository.save(currentItempedido);
        return new ResponseEntity<Itempedido>(currentItempedido, HttpStatus.OK);
    }
 
    // ------------------- Excluir um itempedido-----------------------------------------
 
    @RequestMapping(value = "/itempedido/{codItempedido}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteItempedido(@PathVariable("codItempedido") Integer codItempedido) {
        logger.info("Fetching & Deleting Itempedido with id {}", codItempedido);
 
        Itempedido itempedido = itempedidoRepository.findOne(codItempedido);
        if (itempedido == null) {
            logger.error("Nao foi possivel excluir. Itempedido com codItempedido {} nao encontrado.", codItempedido);
            return new ResponseEntity(new CustomErrorType("Nao foi possivel excluir. Itempedido com codItempedido " + codItempedido + " nao encontrado."),
                    HttpStatus.NOT_FOUND);
        }
        itempedidoRepository.delete(codItempedido);
        return new ResponseEntity<Itempedido>(HttpStatus.NO_CONTENT);
    }
 
    // ------------------- Excluir todos os itempedidos-----------------------------
 
    @RequestMapping(value = "/itempedido/", method = RequestMethod.DELETE)
    public ResponseEntity<Itempedido> deleteAllItempedidos() {
        logger.info("Excluindo todos os Itempedidos");
 
        itempedidoRepository.deleteAll();
        return new ResponseEntity<Itempedido>(HttpStatus.NO_CONTENT);
    }
 
}
