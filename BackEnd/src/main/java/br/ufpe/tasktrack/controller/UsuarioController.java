package br.ufpe.tasktrack.controller;

import br.ufpe.tasktrack.domain.Usuario;
import br.ufpe.tasktrack.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    @Autowired  // Injeção de dependência do repositório
    private UsuarioRepository usuarioRepository;

    // Endpoint GET: http://localhost:8080/usuarios
    @GetMapping
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // Endpoint POST mínimo: http://localhost:8080/usuarios
    @PostMapping
    public Usuario criar(@RequestBody Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
}
