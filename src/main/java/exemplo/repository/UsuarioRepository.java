package exemplo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import exemplo.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
	
	Usuario findByLoginLike(String login);
	
	Usuario findByLoginLikeAndSenhaLike(String login, String senha);

}
