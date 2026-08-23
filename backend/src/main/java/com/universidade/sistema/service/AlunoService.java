package com.universidade.sistema.service;

import com.universidade.sistema.domain.Aluno;
import com.universidade.sistema.domain.Curso;
import com.universidade.sistema.domain.Role;
import com.universidade.sistema.domain.Usuario;
import com.universidade.sistema.dto.AlunoDtos;
import com.universidade.sistema.repository.AlunoRepository;
import com.universidade.sistema.repository.CursoRepository;
import com.universidade.sistema.repository.MatriculaRepository;
import com.universidade.sistema.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MatriculaRepository matriculaRepository;
    private final CursoRepository cursoRepository;
    private final PasswordEncoder passwordEncoder;

    public List<AlunoDtos.AlunoResponse> listar(String q, Long cursoId, Boolean semCurso) {
        List<Aluno> alunos;
        if (q != null && !q.isBlank()) {
            alunos = alunoRepository.search(q.trim());
        } else if (Boolean.TRUE.equals(semCurso)) {
            alunos = alunoRepository.findByCursoIsNull();
        } else if (cursoId != null) {
            alunos = alunoRepository.findByCursoId(cursoId);
        } else {
            alunos = alunoRepository.findAll();
        }
        return alunos.stream().map(this::toResponse).toList();
    }

    public AlunoDtos.AlunoResponse buscar(Long id) {
        return toResponse(getAluno(id));
    }

    @Transactional
    public AlunoDtos.AlunoResponse criar(AlunoDtos.AlunoRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }
        Curso curso = null;
        if (request.cursoId() != null) {
            curso = cursoRepository.findById(request.cursoId())
                    .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));
        }
        String senha = (request.senha() == null || request.senha().isBlank()) ? "aluno123" : request.senha();
        Usuario usuario = usuarioRepository.save(Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(senha))
                .role(Role.ALUNO)
                .ativo(true)
                .build());
        Aluno aluno = alunoRepository.save(Aluno.builder()
                .usuario(usuario)
                .matricula(gerarMatricula())
                .curso(curso)
                .build());
        return toResponse(aluno);
    }

    @Transactional
    public AlunoDtos.AlunoResponse atualizar(Long id, AlunoDtos.AlunoRequest request) {
        Aluno aluno = getAluno(id);
        Usuario usuario = aluno.getUsuario();
        if (!usuario.getEmail().equals(request.email()) && usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }
        Curso curso = null;
        if (request.cursoId() != null) {
            curso = cursoRepository.findById(request.cursoId())
                    .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));
        }
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        if (request.senha() != null && !request.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(request.senha()));
        }
        aluno.setCurso(curso);
        return toResponse(aluno);
    }

    @Transactional
    public AlunoDtos.AlunoResponse matricularEmCurso(Long id, AlunoDtos.MatricularCursoRequest request) {
        Aluno aluno = getAluno(id);
        if (aluno.getCurso() != null) {
            throw new IllegalArgumentException("Aluno já está matriculado em um curso");
        }
        Curso curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));
        aluno.setCurso(curso);
        return toResponse(aluno);
    }

    @Transactional
    public void remover(Long id) {
        Aluno aluno = getAluno(id);
        Long usuarioId = aluno.getUsuario().getId();
        matriculaRepository.findByAlunoId(id).forEach(matriculaRepository::delete);
        alunoRepository.delete(aluno);
        usuarioRepository.deleteById(usuarioId);
    }

    private String gerarMatricula() {
        String prefix = String.valueOf(Year.now().getValue());
        int next = alunoRepository.findMaxMatriculaByPrefix(prefix)
                .map(max -> {
                    try {
                        return Integer.parseInt(max.substring(prefix.length())) + 1;
                    } catch (NumberFormatException ex) {
                        return 1;
                    }
                })
                .orElse(1);
        return prefix + String.format("%04d", next);
    }

    private Aluno getAluno(Long id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
    }

    private AlunoDtos.AlunoResponse toResponse(Aluno aluno) {
        Usuario u = aluno.getUsuario();
        return new AlunoDtos.AlunoResponse(
                aluno.getId(),
                u.getId(),
                u.getNome(),
                u.getEmail(),
                aluno.getMatricula(),
                aluno.getCurso() != null ? aluno.getCurso().getId() : null,
                aluno.getCurso() != null ? aluno.getCurso().getNome() : null,
                u.isAtivo()
        );
    }
}
