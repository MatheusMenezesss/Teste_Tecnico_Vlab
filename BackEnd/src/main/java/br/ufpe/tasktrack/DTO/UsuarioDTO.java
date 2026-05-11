/* DTO que retorna os dados publicos do usuario sem o campo de senha. */
package br.ufpe.tasktrack.DTO;

import br.ufpe.tasktrack.domain.Usuario;

public class UsuarioDTO {

    private Integer idUsuario;
    private String nome;
    private String email;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Integer idUsuario, String nome, String email) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
    }

    public UsuarioDTO(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Usuario toEntity(String senha) {
        return new Usuario(nome, email, senha);
    }
}
