package exemplo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import exemplo.model.Itempedido;

public interface ItempedidoRepository extends JpaRepository<Itempedido, Integer> {
	
	@Query("select p.itempedidos from Pedido p where p.codPedido = ?1")
	public List<Itempedido> findByPedidoByCodPedido(Integer codPedido);

}
