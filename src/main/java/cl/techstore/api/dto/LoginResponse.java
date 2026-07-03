package cl.techstore.api.dto;

public class LoginResponse {

    private String token;
    private String tipo;
    private Long expiracion;

    public LoginResponse() {
    }

    public LoginResponse(String token, String tipo, Long expiracion) {
        this.token = token;
        this.tipo = tipo;
        this.expiracion = expiracion;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getExpiracion() {
        return expiracion;
    }

    public void setExpiracion(Long expiracion) {
        this.expiracion = expiracion;
    }
}
