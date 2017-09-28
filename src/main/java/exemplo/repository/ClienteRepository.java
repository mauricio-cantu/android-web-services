package exemplo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import exemplo.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

	Cliente findByNomeLike(String nome);

}
