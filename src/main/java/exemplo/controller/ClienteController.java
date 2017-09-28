package exemplo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import exemplo.model.Cliente;
import exemplo.repository.ClienteRepository;


@Controller    
@RequestMapping(path="") 
public class ClienteController {
	@Autowired
	private ClienteRepository clienteRepository;


    public static final Logger logger = LoggerFactory.getLogger(ClienteController.class);
    
 
    // -------------------Retorna todos os clientes---------------------------------------------
 
    @RequestMapping(value = "/cliente/", method = RequestMethod.GET)
    public ResponseEntity<List<Cliente>> listaTodosClientes() {
        logger.info("Buscando todos os clientes {}");
    	List<Cliente> clientes = clienteRepository.findAll();
        if (clientes.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
            // You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<List<Cliente>>(clientes, HttpStatus.OK);
    }
 
    // -------------------Retorna um cliente pelo ID------------------------------------------
 
    @RequestMapping(value = "/cliente/{codCliente}", method = RequestMethod.GET)
    public ResponseEntity<?> getClienteById(@PathVariable("codCliente") Integer codCliente) {
        logger.info("Buscando cliente com codCliente {}", codCliente);
        Cliente cliente = clienteRepository.findOne(codCliente);
        if (cliente == null) {
            logger.error("Cliente com codCliente {} nao encontrado.", codCliente);
            return new ResponseEntity(new CustomErrorType("Cliente com codCliente " + codCliente 
                    + " nao encontrado"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
    }

    @RequestMapping(value = "/cliente", method = RequestMethod.GET)
    public ResponseEntity<?> getClienteByNome(@RequestParam(value="nome") String nome) {
        logger.info("Buscando Cliente com a nome {}", nome);
        Cliente cliente = clienteRepository.findByNomeLike(nome);
        if (cliente == null) {
            logger.error("Cliente com a nome {} nao encontrado.", nome);
            return new ResponseEntity(new CustomErrorType("Cliente com nome " + nome 
                    + " nao encontrado"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
    }
    
    
    // -------------------Cadastrar um Cliente-------------------------------------------
 
    @RequestMapping(value = "/cliente/", method = RequestMethod.POST)
    public ResponseEntity<?> cadastrarCliente(@RequestBody Cliente cliente, UriComponentsBuilder ucBuilder) {
        logger.info("Cadastrando Cliente : {}", cliente.getNome());
 
        if (clienteRepository.findByNomeLike(cliente.getNome()) != null) {
            logger.error("Nao foi possivel cadastrar. Um Cliente com a nome {} ja existe", cliente.getNome());
            return new ResponseEntity(new CustomErrorType("Nao foi possivel cadastrar. Um Cliente com a nome " + 
            cliente.getNome() + " ja existe."),HttpStatus.CONFLICT);
        }
        Cliente c = clienteRepository.save(cliente);
 
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/cliente/{codCliente}").buildAndExpand(c.getCodCliente()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
 
    // ------------------- Atualizar um cliente------------------------------------------------
 
    @RequestMapping(value = "/cliente/{codCliente}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateCliente(@PathVariable("codCliente") Integer codCliente, @RequestBody Cliente cliente) {
        logger.info("Atualizando o Cliente com codCliente {}", codCliente);
 
        Cliente currentCliente = clienteRepository.findOne(codCliente);
 
        if (currentCliente == null) {
            logger.error("Nao foi possivel atualizar. Cliente com codCliente {} nao encontrado.", codCliente);
            return new ResponseEntity(new CustomErrorType("Nao foi possivel atualizar. Cliente com codCliente " + codCliente + " nao encontrado."),
                    HttpStatus.NOT_FOUND);
        }
 
        currentCliente.setNome(cliente.getNome());
        currentCliente.setEndereco(cliente.getEndereco());
        currentCliente.setCep(cliente.getCep());
        currentCliente.setCidade(cliente.getCidade());
        currentCliente.setIe(cliente.getIe());
        currentCliente.setUf(cliente.getUf());
 
        clienteRepository.save(currentCliente);
        return new ResponseEntity<Cliente>(currentCliente, HttpStatus.OK);
    }
 
    // ------------------- Excluir um cliente-----------------------------------------
 
    @RequestMapping(value = "/cliente/{codCliente}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCliente(@PathVariable("codCliente") Integer codCliente) {
        logger.info("Fetching & Deleting Cliente with id {}", codCliente);
 
        Cliente cliente = clienteRepository.findOne(codCliente);
        if (cliente == null) {
            logger.error("Nao foi possivel excluir. Cliente com codCliente {} nao encontrado.", codCliente);
            return new ResponseEntity(new CustomErrorType("Nao foi possivel excluir. Cliente com codCliente " + codCliente + " nao encontrado."),
                    HttpStatus.NOT_FOUND);
        }
        clienteRepository.delete(codCliente);
        return new ResponseEntity<Cliente>(HttpStatus.NO_CONTENT);
    }
 
    // ------------------- Excluir todos os clientes-----------------------------
 
    @RequestMapping(value = "/cliente/", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAllClientes() {
        logger.info("Excluindo todos os Clientes");
 
        clienteRepository.deleteAll();
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
 
}
