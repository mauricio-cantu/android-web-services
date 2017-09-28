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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import exemplo.model.Produto;
import exemplo.repository.ProdutoRepository;


@Controller    
@RequestMapping(path="") 
public class ProdutoController {
	@Autowired
	private ProdutoRepository produtoRepository;


    public static final Logger logger = LoggerFactory.getLogger(ProdutoController.class);
    
 
    // -------------------Retorna todos os produtos---------------------------------------------
 
    @RequestMapping(value = "/produto/", method = RequestMethod.GET)
    public ResponseEntity<List<Produto>> listAllProdutos() {
        logger.info("Buscando todos os produtos {}");
    	List<Produto> produtos = produtoRepository.findAll();
        if (produtos.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
            // You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<List<Produto>>(produtos, HttpStatus.OK);
    }
 
    // -------------------Retorna um produto pelo ID------------------------------------------
 
    @RequestMapping(value = "/produto/{codProduto}", method = RequestMethod.GET)
    public ResponseEntity<?> getProdutoById(@PathVariable("codProduto") Integer codProduto) {
        logger.info("Buscando produto com codProduto {}", codProduto);
        Produto produto = produtoRepository.findOne(codProduto);
        if (produto == null) {
            logger.error("Produto com codProduto {} nao encontrado.", codProduto);
            return new ResponseEntity(new CustomErrorType("Produto com codProduto " + codProduto 
                    + " nao encontrado"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Produto>(produto, HttpStatus.OK);
    }

    @RequestMapping(value = "/produto", method = RequestMethod.GET)
    public ResponseEntity<?> getProdutoByDescricao(@RequestParam(value="descricao") String descricao) {
        logger.info("Buscando Produto com a descricao {}", descricao);
        Produto produto = produtoRepository.findByDescricaoLike(descricao);
        if (produto == null) {
            logger.error("Produto com a descricao {} nao encontrado.", descricao);
            return new ResponseEntity(new CustomErrorType("Produto com a descricao " + descricao 
                    + " nao encontrado"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Produto>(produto, HttpStatus.OK);
    }
    
    
    // -------------------Cadastrar um Produto-------------------------------------------
 
    @RequestMapping(value = "/produto/", method = RequestMethod.POST)
    public ResponseEntity<?> cadastrarProduto(@RequestBody Produto produto, UriComponentsBuilder ucBuilder) {
        logger.info("Cadastrando Produto : {}", produto.getDescricao());
 
        if (produtoRepository.findByDescricaoLike(produto.getDescricao()) != null) {
            logger.error("Nao foi possivel cadastrar. Um Produto com a descricao {} ja existe", produto.getDescricao());
            return new ResponseEntity(new CustomErrorType("Nao foi possivel cadastrar. Um Produto com a descricao " + 
            produto.getDescricao() + " ja existe."),HttpStatus.CONFLICT);
        }
        Produto p = produtoRepository.save(produto);
 
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/produto/{codProduto}").buildAndExpand(p.getCodProduto()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
 
    // ------------------- Atualizar um produto------------------------------------------------
 
    @RequestMapping(value = "/produto/{codProduto}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateProduto(@PathVariable("codProduto") Integer codProduto, @RequestBody Produto produto) {
        logger.info("Atualizando o Produto com codProduto {}", codProduto);
 
        Produto currentProduto = produtoRepository.findOne(codProduto);
 
        if (currentProduto == null) {
            logger.error("Nao foi possivel atualizar. Produto com codProduto {} nao encontrado.", codProduto);
            return new ResponseEntity(new CustomErrorType("Nao foi possivel atualizar. Produto com codProduto " + codProduto + " nao encontrado."),
                    HttpStatus.NOT_FOUND);
        }
 
        currentProduto.setDescricao(produto.getDescricao());
        currentProduto.setValorUnitario(produto.getValorUnitario());
 
        produtoRepository.save(currentProduto);
        return new ResponseEntity<Produto>(currentProduto, HttpStatus.OK);
    }
 
    // ------------------- Excluir um produto-----------------------------------------
 
    @RequestMapping(value = "/produto/{codProduto}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteProduto(@PathVariable("codProduto") Integer codProduto) {
        logger.info("Fetching & Deleting Produto with id {}", codProduto);
 
        Produto produto = produtoRepository.findOne(codProduto);
        if (produto == null) {
            logger.error("Nao foi possivel excluir. Produto com codProduto {} nao encontrado.", codProduto);
            return new ResponseEntity(new CustomErrorType("Nao foi possivel excluir. Produto com codProduto " + codProduto + " nao encontrado."),
                    HttpStatus.NOT_FOUND);
        }
        produtoRepository.delete(codProduto);
        return new ResponseEntity<Produto>(HttpStatus.NO_CONTENT);
    }
 
    // ------------------- Excluir todos os produtos-----------------------------
 
    @RequestMapping(value = "/produto/", method = RequestMethod.DELETE)
    public ResponseEntity<Produto> deleteAllProdutos() {
        logger.info("Excluindo todos os Produtos");
 
        produtoRepository.deleteAll();
        return new ResponseEntity<Produto>(HttpStatus.NO_CONTENT);
    }
 
}
