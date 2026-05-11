package br.ufpe.tasktrack.service;

import br.ufpe.tasktrack.DTO.UsuarioDTO;
import br.ufpe.tasktrack.domain.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    UsuarioDTO createUser(Usuario usuario);

    List<UsuarioDTO> getAllUsers();

    UsuarioDTO updateUser(Integer id_usuario, Usuario usuario);

    void deleteUser(Integer id_usuario);

    Optional<UsuarioDTO> getUserById(Integer id_usuario);
 
}
