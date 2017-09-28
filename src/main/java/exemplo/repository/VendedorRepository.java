package exemplo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import exemplo.model.Vendedor;



public interface VendedorRepository extends JpaRepository<Vendedor, Integer> {

	Vendedor findByNomeLike(String nome);

}
