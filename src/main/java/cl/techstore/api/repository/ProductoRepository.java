package cl.techstore.api.repository;

import cl.techstore.api.model.Producto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findAllByActivoTrueOrderByIdAsc();

    Optional<Producto> findByIdAndActivoTrue(Long id);
}
