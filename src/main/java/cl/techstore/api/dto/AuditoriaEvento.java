package cl.techstore.api.dto;

public class AuditoriaEvento {

    private final String accion;
    private final Long productoId;
    private final String usuario;
    private final String fecha;

    public AuditoriaEvento(String accion, Long productoId, String usuario, String fecha) {
        this.accion = accion;
        this.productoId = productoId;
        this.usuario = usuario;
        this.fecha = fecha;
    }

    public String getAccion() {
        return accion;
    }

    public Long getProductoId() {
        return productoId;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getFecha() {
        return fecha;
    }
}
