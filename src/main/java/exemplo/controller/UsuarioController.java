package exemplo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import exemplo.model.Cliente;
import exemplo.model.Usuario;
import exemplo.repository.UsuarioRepository;

@Controller    
@RequestMapping(path="/usuario/")
public class UsuarioController {
	
	@Autowired
	private UsuarioRepository userRep;
	
	@RequestMapping(value="{login}/{senha}", method=RequestMethod.POST)	
	public ResponseEntity<Usuario> autentica(@PathVariable String login, @PathVariable String senha){
		
		Usuario usuarioBD;
		
		if(userRep.findByLoginLike(login) == null) {
			return new ResponseEntity(new CustomErrorType("Login não encontrado!"), HttpStatus.NOT_FOUND);
		}else {
			usuarioBD = userRep.findByLoginLikeAndSenhaLike(login, senha);
			if (usuarioBD == null) {       
	            return new ResponseEntity(new CustomErrorType("Senha inválida!"), HttpStatus.NOT_FOUND);
	        }
		}
		
        return new ResponseEntity<Usuario>(usuarioBD, HttpStatus.OK);
	}
	
	@RequestMapping(value="", method=RequestMethod.POST)
	public ResponseEntity<Usuario> postCadastrar(@RequestBody Usuario u){
		
		Usuario userCadastrado = userRep.save(u);
		
		return new ResponseEntity<Usuario>(userCadastrado, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "", method=RequestMethod.PUT)
	public ResponseEntity<Usuario> putAtualizar(@RequestBody Usuario usuario){
		
		userRep.save(usuario);
		
		return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Integer id){
		if(userRep.findOne(id) == null) {
			return new ResponseEntity<>("Usuário não encontrado", HttpStatus.NOT_FOUND);
		}
		
		userRep.delete(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping
	public ResponseEntity<List<Usuario>> findAll(){
		List<Usuario> listaUsers = userRep.findAll();
	
		return new ResponseEntity(listaUsers, HttpStatus.OK);
	}
	
	@GetMapping(value = "{id}")
	public ResponseEntity<?> findById(@PathVariable Integer id){
		
		if(userRep.findOne(id) == null) {
			return new ResponseEntity<>("Usuário não encontrado", HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Usuario>(userRep.findOne(id), HttpStatus.OK);
		
	}
	
}
