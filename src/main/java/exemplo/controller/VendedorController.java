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

import exemplo.model.Vendedor;
import exemplo.repository.VendedorRepository;



@Controller    
@RequestMapping(path="") 
public class VendedorController {
	@Autowired
	private VendedorRepository vendedorRepository;


    public static final Logger logger = LoggerFactory.getLogger(VendedorController.class);
    
 
    // -------------------Retorna todos os vendedors---------------------------------------------
 
    @RequestMapping(value = "/vendedor/", method = RequestMethod.GET)
    public ResponseEntity<List<Vendedor>> listaTodosVendedors() {
        logger.info("Buscando todos os vendedors {}");
    	List<Vendedor> vendedors = vendedorRepository.findAll();
        if (vendedors.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
            // You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<List<Vendedor>>(vendedors, HttpStatus.OK);
    }
 
    // -------------------Retorna um vendedor pelo ID------------------------------------------
 
    @RequestMapping(value = "/vendedor/{codVendedor}", method = RequestMethod.GET)
    public ResponseEntity<?> getVendedorById(@PathVariable("codVendedor") Integer codVendedor) {
        logger.info("Buscando vendedor com codVendedor {}", codVendedor);
        Vendedor vendedor = vendedorRepository.findOne(codVendedor);
        if (vendedor == null) {
            logger.error("Vendedor com codVendedor {} nao encontrado.", codVendedor);
            return new ResponseEntity(new CustomErrorType("Vendedor com codVendedor " + codVendedor 
                    + " nao encontrado"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Vendedor>(vendedor, HttpStatus.OK);
    }

    @RequestMapping(value = "/vendedor", method = RequestMethod.GET)
    public ResponseEntity<?> getVendedorByDescricao(@RequestParam(value="descricao") String descricao) {
        logger.info("Buscando Vendedor com a descricao {}", descricao);
        Vendedor vendedor = vendedorRepository.findByNomeLike(descricao);
        if (vendedor == null) {
            logger.error("Vendedor com a descricao {} nao encontrado.", descricao);
            return new ResponseEntity(new CustomErrorType("Vendedor com a descricao " + descricao 
                    + " nao encontrado"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Vendedor>(vendedor, HttpStatus.OK);
    }
    
    
    // -------------------Cadastrar um Vendedor-------------------------------------------
 
    @RequestMapping(value = "/vendedor/", method = RequestMethod.POST)
    public ResponseEntity<?> cadastrarVendedor(@RequestBody Vendedor vendedor, UriComponentsBuilder ucBuilder) {
        logger.info("Cadastrando Vendedor : {}", vendedor.getNome());
 
        if (vendedorRepository.findByNomeLike(vendedor.getNome()) != null) {
            logger.error("Nao foi possivel cadastrar. Um Vendedor com a descricao {} ja existe", vendedor.getNome());
            return new ResponseEntity(new CustomErrorType("Nao foi possivel cadastrar. Um Vendedor com a descricao " + 
            vendedor.getNome() + " ja existe."),HttpStatus.CONFLICT);
        }
        Vendedor v = vendedorRepository.save(vendedor);
 
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/vendedor/{codVendedor}").buildAndExpand(v.getCodVendedor()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
 
    // ------------------- Atualizar um vendedor------------------------------------------------
 
    @RequestMapping(value = "/vendedor/{codVendedor}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateVendedor(@PathVariable("codVendedor") Integer codVendedor, @RequestBody Vendedor vendedor) {
        logger.info("Atualizando o Vendedor com codVendedor {}", codVendedor);
 
        Vendedor currentVendedor = vendedorRepository.findOne(codVendedor);
 
        if (currentVendedor == null) {
            logger.error("Nao foi possivel atualizar. Vendedor com codVendedor {} nao encontrado.", codVendedor);
            return new ResponseEntity(new CustomErrorType("Nao foi possivel atualizar. Vendedor com codVendedor " + codVendedor + " nao encontrado."),
                    HttpStatus.NOT_FOUND);
        }
 
        currentVendedor.setNome(vendedor.getNome());
        currentVendedor.setFaixaComissao(vendedor.getFaixaComissao());
        currentVendedor.setSalarioFixo(vendedor.getSalarioFixo());
 
        vendedorRepository.save(currentVendedor);
        return new ResponseEntity<Vendedor>(currentVendedor, HttpStatus.OK);
    }
 
    // ------------------- Excluir um vendedor-----------------------------------------
 
    @RequestMapping(value = "/vendedor/{codVendedor}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteVendedor(@PathVariable("codVendedor") Integer codVendedor) {
        logger.info("Fetching & Deleting Vendedor with id {}", codVendedor);
 
        Vendedor vendedor = vendedorRepository.findOne(codVendedor);
        if (vendedor == null) {
            logger.error("Nao foi possivel excluir. Vendedor com codVendedor {} nao encontrado.", codVendedor);
            return new ResponseEntity(new CustomErrorType("Nao foi possivel excluir. Vendedor com codVendedor " + codVendedor + " nao encontrado."),
                    HttpStatus.NOT_FOUND);
        }
        vendedorRepository.delete(codVendedor);
        return new ResponseEntity<Vendedor>(HttpStatus.NO_CONTENT);
    }
 
    // ------------------- Excluir todos os vendedors-----------------------------
 
    @RequestMapping(value = "/vendedor/", method = RequestMethod.DELETE)
    public ResponseEntity<Vendedor> deleteAllVendedors() {
        logger.info("Excluindo todos os Vendedors");
 
        vendedorRepository.deleteAll();
        return new ResponseEntity<Vendedor>(HttpStatus.NO_CONTENT);
    }
 
}
