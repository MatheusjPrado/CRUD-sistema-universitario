package com.universidade.sistema.service;

import com.universidade.sistema.domain.Aluno;
import com.universidade.sistema.domain.Materia;
import com.universidade.sistema.domain.Matricula;
import com.universidade.sistema.domain.Professor;
import com.universidade.sistema.domain.Turma;
import com.universidade.sistema.dto.MatriculaDtos;
import com.universidade.sistema.repository.AlunoRepository;
import com.universidade.sistema.repository.MateriaRepository;
import com.universidade.sistema.repository.MatriculaRepository;
import com.universidade.sistema.repository.ProfessorRepository;
import com.universidade.sistema.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;
    private final MateriaRepository materiaRepository;
    private final ProfessorRepository professorRepository;

    public List<MatriculaDtos.MatriculaResponse> listar() {
        return matriculaRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<MatriculaDtos.MatriculaResponse> listarPorAluno(Long alunoId) {
        return matriculaRepository.findByAlunoId(alunoId).stream().map(this::toResponse).toList();
    }

    public List<MatriculaDtos.MatriculaResponse> listarPorTurma(Long turmaId) {
        return matriculaRepository.findByTurmaId(turmaId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public MatriculaDtos.MatriculaResponse criar(MatriculaDtos.MatriculaRequest request) {
        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
        Turma turma = turmaRepository.findById(request.turmaId())
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada"));
        return matricularAlunoNaTurma(aluno, turma);
    }

    @Transactional
    public MatriculaDtos.MatriculaResponse matricularEmMateria(MatriculaDtos.MatriculaMateriaRequest request) {
        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
        if (aluno.getCurso() == null) {
            throw new IllegalArgumentException("Aluno precisa estar matriculado em um curso primeiro");
        }
        Materia materia = materiaRepository.findById(request.materiaId())
                .orElseThrow(() -> new IllegalArgumentException("Matéria não encontrada"));
        if (!aluno.getCurso().getId().equals(materia.getCurso().getId())) {
            throw new IllegalArgumentException("Aluno só pode se matricular em matérias do próprio curso");
        }

        Turma turma = turmaRepository.findByMateriaId(materia.getId()).stream()
                .max(Comparator.comparing(Turma::getAno).thenComparing(Turma::getId))
                .orElseGet(() -> criarTurmaPadrao(materia));

        return matricularAlunoNaTurma(aluno, turma);
    }

    @Transactional
    public MatriculaDtos.MatriculaResponse lancarNota(Long id, MatriculaDtos.NotaRequest request) {
        if (request.nota() < 0 || request.nota() > 10) {
            throw new IllegalArgumentException("Nota deve estar entre 0 e 10");
        }
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Matrícula não encontrada"));
        matricula.setNota(request.nota());
        return toResponse(matricula);
    }

    @Transactional
    public void remover(Long id) {
        if (!matriculaRepository.existsById(id)) {
            throw new IllegalArgumentException("Matrícula não encontrada");
        }
        matriculaRepository.deleteById(id);
    }

    private MatriculaDtos.MatriculaResponse matricularAlunoNaTurma(Aluno aluno, Turma turma) {
        if (matriculaRepository.existsByAlunoIdAndTurmaId(aluno.getId(), turma.getId())) {
            throw new IllegalArgumentException("Aluno já matriculado nesta matéria/turma");
        }
        if (aluno.getCurso() == null) {
            throw new IllegalArgumentException("Aluno precisa estar matriculado em um curso primeiro");
        }
        if (!aluno.getCurso().getId().equals(turma.getMateria().getCurso().getId())) {
            throw new IllegalArgumentException("Aluno só pode se matricular em matérias do próprio curso");
        }
        Matricula matricula = matriculaRepository.save(Matricula.builder()
                .aluno(aluno)
                .turma(turma)
                .build());
        return toResponse(matricula);
    }

    private Turma criarTurmaPadrao(Materia materia) {
        Professor professor = professorRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Não há professor cadastrado. Cadastre um professor ou crie uma turma para a matéria."
                ));
        return turmaRepository.save(Turma.builder()
                .materia(materia)
                .professor(professor)
                .semestre("1")
                .ano(Year.now().getValue())
                .build());
    }

    private MatriculaDtos.MatriculaResponse toResponse(Matricula m) {
        return new MatriculaDtos.MatriculaResponse(
                m.getId(),
                m.getAluno().getId(),
                m.getAluno().getUsuario().getNome(),
                m.getAluno().getMatricula(),
                m.getTurma().getId(),
                m.getTurma().getMateria().getId(),
                m.getTurma().getMateria().getNome(),
                m.getTurma().getMateria().getCurso().getNome(),
                m.getTurma().getSemestre(),
                m.getTurma().getAno(),
                m.getNota()
        );
    }
}
