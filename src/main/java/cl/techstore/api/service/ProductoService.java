package cl.techstore.api.service;

import cl.techstore.api.model.Producto;
import cl.techstore.api.repository.ProductoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    @Transactional
    public Producto crear(Producto producto) {
        producto.setId(null);
        return productoRepository.save(producto);
    }

    @Transactional
    public Producto actualizar(Long id, Producto productoActualizado) {
        Producto productoExistente = obtenerPorId(id);

        productoExistente.setNombre(productoActualizado.getNombre());
        productoExistente.setDescripcion(productoActualizado.getDescripcion());
        productoExistente.setPrecio(productoActualizado.getPrecio());
        productoExistente.setStock(productoActualizado.getStock());
        productoExistente.setCategoria(productoActualizado.getCategoria());
        productoExistente.setActivo(productoActualizado.getActivo());

        return productoRepository.save(productoExistente);
    }

    @Transactional
    public void eliminar(Long id) {
        Producto producto = obtenerPorId(id);
        productoRepository.delete(producto);
    }
}
