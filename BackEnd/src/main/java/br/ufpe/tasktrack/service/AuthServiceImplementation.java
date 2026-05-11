package br.ufpe.tasktrack.service;

import br.ufpe.tasktrack.DTO.AuthResponseDTO;
import br.ufpe.tasktrack.DTO.LoginDTO;
import br.ufpe.tasktrack.DTO.RegisterDTO;
import br.ufpe.tasktrack.domain.Usuario;
import br.ufpe.tasktrack.repository.UsuarioRepository;
import br.ufpe.tasktrack.security.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImplementation implements AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AuthResponseDTO login(LoginDTO loginDTO) {
        Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(loginDTO.getSenha(), usuario.getSenha())) {
            throw new RuntimeException("Senha incorreta");
        }

        String token = jwtTokenProvider.gerarToken(usuario.getEmail());
        return new AuthResponseDTO(token, usuario.getEmail(), usuario.getIdUsuario());
    }

    @Override
    public AuthResponseDTO register(RegisterDTO registerDTO) {
        //verificar a existencia do email cadastrado
        if (usuarioRepository.existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("Email já registrado");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(registerDTO.getNome());
        novoUsuario.setEmail(registerDTO.getEmail());
        novoUsuario.setSenha(passwordEncoder.encode(registerDTO.getSenha()));

        usuarioRepository.save(novoUsuario);
        
        String token = jwtTokenProvider.gerarToken(novoUsuario.getEmail());
        return new AuthResponseDTO(token, novoUsuario.getEmail(), novoUsuario.getIdUsuario());
    }
}
