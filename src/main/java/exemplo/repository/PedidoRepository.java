package exemplo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import exemplo.model.Pedido;


public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

}
