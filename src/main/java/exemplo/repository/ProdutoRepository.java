package exemplo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import exemplo.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

	Produto findByDescricaoLike(String descricao);

}
