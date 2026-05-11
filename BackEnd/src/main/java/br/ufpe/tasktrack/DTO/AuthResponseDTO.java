/* DTO de resposta do login e cadastro, retornando token e identificacao do usuario. */
package br.ufpe.tasktrack.DTO;

public class AuthResponseDTO {
    private String token;
    private String email;
    private Integer idUsuario;

    public AuthResponseDTO(String token, String email, Integer idUsuario) {
        this.token = token;
        this.email = email;
        this.idUsuario = idUsuario;
    }

    // Getters
    public String getToken() { return token; }
    public String getEmail() { return email; }
    public Integer getIdUsuario() { return idUsuario; }
}
