package br.ufpe.tasktrack.service;

import br.ufpe.tasktrack.DTO.UsuarioDTO;
import br.ufpe.tasktrack.domain.Usuario;
import br.ufpe.tasktrack.repository.UsuarioRepository;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioServiceImplementation implements UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImplementation.class);
    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImplementation(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UsuarioDTO createUser(Usuario usuario){
        Usuario salvo = usuarioRepository.save(usuario);
        return new UsuarioDTO(salvo);
    }

    @Override
    public List<UsuarioDTO> getAllUsers(){
        return usuarioRepository.findAll()
            .stream()
            .map(UsuarioDTO::new)
            .collect(Collectors.toList());
    }

    @Override
        public UsuarioDTO updateUser(Integer id_usuario, Usuario usuario){
            Usuario user = usuarioRepository.findById(id_usuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id_usuario));
            user.atualizarDados(usuario.getNome(), usuario.getEmail(), usuario.getSenha());
            Usuario salvo = usuarioRepository.save(user);
            return new UsuarioDTO(salvo);
    }

    @Override
    public void deleteUser(Integer id_usuario){
        logger.info("Deletando usuário com ID: " + id_usuario);
        usuarioRepository.deleteById(id_usuario);
    }

    @Override
    public Optional<UsuarioDTO> getUserById(Integer id_usuario){
        return usuarioRepository.findById(id_usuario).map(UsuarioDTO::new);

    }
 

}
