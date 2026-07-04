package cl.techstore.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.techstore.api.dto.LoginResponse;
import cl.techstore.api.dto.ProductoDTO;
import cl.techstore.api.model.Producto;
import cl.techstore.api.repository.ProductoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProductoRepository productoRepository;

    @BeforeEach
    void limpiarDatos() {
        productoRepository.deleteAll();
    }

    @Test
    void listarProductosSinTokenRetornaForbidden() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isForbidden());
    }

    @Test
    void crudDeProductoConJwtFunciona() throws Exception {
        String token = obtenerToken();

        ProductoDTO nuevoProducto = new ProductoDTO();
        nuevoProducto.setNombre("Notebook Gamer");
        nuevoProducto.setDescripcion("RTX 4060 y 16GB RAM");
        nuevoProducto.setPrecio(899990.0);
        nuevoProducto.setStock(8);
        nuevoProducto.setCategoria("Electronica");
        nuevoProducto.setActivo(true);

        MvcResult createResult = mockMvc.perform(post("/api/productos")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoProducto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombre").value("Notebook Gamer"))
                .andExpect(jsonPath("$.categoria").value("Electronica"))
                .andReturn();

        ProductoDTO productoCreado = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), ProductoDTO.class);

        mockMvc.perform(get("/api/productos")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(productoCreado.getId()))
                .andExpect(jsonPath("$[0].nombre").value("Notebook Gamer"));

        ProductoDTO productoActualizado = new ProductoDTO();
        productoActualizado.setNombre("Notebook Gamer Pro");
        productoActualizado.setDescripcion("RTX 4070 y 32GB RAM");
        productoActualizado.setPrecio(1299990.0);
        productoActualizado.setStock(5);
        productoActualizado.setCategoria("Electronica");
        productoActualizado.setActivo(true);

        mockMvc.perform(put("/api/productos/{id}", productoCreado.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productoCreado.getId()))
                .andExpect(jsonPath("$.nombre").value("Notebook Gamer Pro"))
                .andExpect(jsonPath("$.stock").value(5));

        mockMvc.perform(delete("/api/productos/{id}", productoCreado.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(token)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/productos")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        Producto productoEliminado = productoRepository.findById(productoCreado.getId()).orElseThrow();
        assertFalse(productoEliminado.getActivo());

        mockMvc.perform(get("/api/productos/{id}", productoCreado.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(token)))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerProductoInexistenteRetornaNotFound() throws Exception {
        String token = obtenerToken();

        mockMvc.perform(get("/api/productos/{id}", 999L)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(token)))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizarProductoInexistenteRetornaNotFound() throws Exception {
        String token = obtenerToken();

        ProductoDTO producto = new ProductoDTO();
        producto.setNombre("Monitor 27");
        producto.setDescripcion("Resolucion QHD");
        producto.setPrecio(249990.0);
        producto.setStock(4);
        producto.setCategoria("Electronica");
        producto.setActivo(true);

        mockMvc.perform(put("/api/productos/{id}", 999L)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarProductoInexistenteRetornaNotFound() throws Exception {
        String token = obtenerToken();

        mockMvc.perform(delete("/api/productos/{id}", 999L)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(token)))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearProductoIgnoraActivoEnFalseYLoPersisteComoActivo() throws Exception {
        String token = obtenerToken();

        ProductoDTO nuevoProducto = new ProductoDTO();
        nuevoProducto.setNombre("Mouse Gamer");
        nuevoProducto.setDescripcion("Sensor optico");
        nuevoProducto.setPrecio(29990.0);
        nuevoProducto.setStock(12);
        nuevoProducto.setCategoria("Electronica");
        nuevoProducto.setActivo(false);

        MvcResult createResult = mockMvc.perform(post("/api/productos")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoProducto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.activo").value(true))
                .andReturn();

        ProductoDTO productoCreado = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), ProductoDTO.class);

        Producto productoPersistido = productoRepository.findById(productoCreado.getId()).orElseThrow();
        assertTrue(productoPersistido.getActivo());
    }

    @Test
    void crearProductoConDatosInvalidosRetornaBadRequest() throws Exception {
        String token = obtenerToken();

        ProductoDTO productoInvalido = new ProductoDTO();
        productoInvalido.setNombre("");
        productoInvalido.setDescripcion("Producto sin datos validos");
        productoInvalido.setPrecio(-10.0);
        productoInvalido.setStock(-1);
        productoInvalido.setCategoria("");
        productoInvalido.setActivo(null);

        mockMvc.perform(post("/api/productos")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarProductoConDatosInvalidosRetornaBadRequest() throws Exception {
        String token = obtenerToken();

        ProductoDTO productoValido = new ProductoDTO();
        productoValido.setNombre("Teclado mecanico");
        productoValido.setDescripcion("Switches red");
        productoValido.setPrecio(79990.0);
        productoValido.setStock(10);
        productoValido.setCategoria("Electronica");
        productoValido.setActivo(true);

        MvcResult createResult = mockMvc.perform(post("/api/productos")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productoValido)))
                .andExpect(status().isCreated())
                .andReturn();

        ProductoDTO productoCreado = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), ProductoDTO.class);

        ProductoDTO productoInvalido = new ProductoDTO();
        productoInvalido.setNombre(" ");
        productoInvalido.setDescripcion("Actualizacion invalida");
        productoInvalido.setPrecio(0.0);
        productoInvalido.setStock(-5);
        productoInvalido.setCategoria("");
        productoInvalido.setActivo(null);

        mockMvc.perform(put("/api/productos/{id}", productoCreado.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productoInvalido)))
                .andExpect(status().isBadRequest());
    }

    private String obtenerToken() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "admin@techstore.cl");
        loginRequest.put("password", "Admin12345");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse loginResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(), LoginResponse.class);

        return loginResponse.getToken();
    }

    private String bearerToken(String token) {
        return "Bearer " + token;
    }
}
