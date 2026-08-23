package com.universidade.sistema.service;

import com.universidade.sistema.domain.Professor;
import com.universidade.sistema.domain.Role;
import com.universidade.sistema.domain.Usuario;
import com.universidade.sistema.dto.ProfessorDtos;
import com.universidade.sistema.repository.ProfessorRepository;
import com.universidade.sistema.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<ProfessorDtos.ProfessorResponse> listar() {
        return professorRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ProfessorDtos.ProfessorResponse buscar(Long id) {
        return toResponse(getProfessor(id));
    }

    @Transactional
    public ProfessorDtos.ProfessorResponse criar(ProfessorDtos.ProfessorRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }
        String senha = (request.senha() == null || request.senha().isBlank()) ? "prof123" : request.senha();
        Usuario usuario = usuarioRepository.save(Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(senha))
                .role(Role.PROFESSOR)
                .ativo(true)
                .build());
        Professor professor = professorRepository.save(Professor.builder()
                .usuario(usuario)
                .departamento(request.departamento())
                .build());
        return toResponse(professor);
    }

    @Transactional
    public ProfessorDtos.ProfessorResponse atualizar(Long id, ProfessorDtos.ProfessorRequest request) {
        Professor professor = getProfessor(id);
        Usuario usuario = professor.getUsuario();
        if (!usuario.getEmail().equals(request.email()) && usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        if (request.senha() != null && !request.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(request.senha()));
        }
        professor.setDepartamento(request.departamento());
        return toResponse(professor);
    }

    @Transactional
    public void remover(Long id) {
        Professor professor = getProfessor(id);
        Long usuarioId = professor.getUsuario().getId();
        professorRepository.delete(professor);
        usuarioRepository.deleteById(usuarioId);
    }

    private Professor getProfessor(Long id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado"));
    }

    private ProfessorDtos.ProfessorResponse toResponse(Professor professor) {
        Usuario u = professor.getUsuario();
        return new ProfessorDtos.ProfessorResponse(
                professor.getId(),
                u.getId(),
                u.getNome(),
                u.getEmail(),
                professor.getDepartamento(),
                u.isAtivo()
        );
    }
}
