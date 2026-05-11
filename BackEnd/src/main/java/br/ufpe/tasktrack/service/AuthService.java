package br.ufpe.tasktrack.service;

import br.ufpe.tasktrack.DTO.AuthResponseDTO;
import br.ufpe.tasktrack.DTO.LoginDTO;
import br.ufpe.tasktrack.DTO.RegisterDTO;

public interface AuthService {
    AuthResponseDTO login(LoginDTO loginDTO);
    AuthResponseDTO register(RegisterDTO registerDTO);
}
