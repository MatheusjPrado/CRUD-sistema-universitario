package com.universidade.sistema.service;

import com.universidade.sistema.domain.Aluno;
import com.universidade.sistema.domain.Professor;
import com.universidade.sistema.domain.Usuario;
import com.universidade.sistema.dto.AuthDtos;
import com.universidade.sistema.repository.AlunoRepository;
import com.universidade.sistema.repository.ProfessorRepository;
import com.universidade.sistema.repository.UsuarioRepository;
import com.universidade.sistema.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final JwtService jwtService;

    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha())
        );
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        String token = jwtService.generateToken(principal, Map.of(
                "role", usuario.getRole().name(),
                "uid", usuario.getId()
        ));

        return new AuthDtos.LoginResponse(
                token,
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole()
        );
    }

    public AuthDtos.MeResponse me(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        Long alunoId = alunoRepository.findByUsuarioId(usuario.getId()).map(Aluno::getId).orElse(null);
        Long professorId = professorRepository.findByUsuarioId(usuario.getId()).map(Professor::getId).orElse(null);
        return new AuthDtos.MeResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                alunoId,
                professorId
        );
    }
}
