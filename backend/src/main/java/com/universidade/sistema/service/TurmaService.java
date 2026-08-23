package com.universidade.sistema.service;

import com.universidade.sistema.domain.Materia;
import com.universidade.sistema.domain.Professor;
import com.universidade.sistema.domain.Turma;
import com.universidade.sistema.dto.TurmaDtos;
import com.universidade.sistema.repository.MateriaRepository;
import com.universidade.sistema.repository.ProfessorRepository;
import com.universidade.sistema.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final MateriaRepository materiaRepository;
    private final ProfessorRepository professorRepository;

    public List<TurmaDtos.TurmaResponse> listar() {
        return turmaRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<TurmaDtos.TurmaResponse> listarPorProfessor(Long professorId) {
        return turmaRepository.findByProfessorId(professorId).stream().map(this::toResponse).toList();
    }

    public TurmaDtos.TurmaResponse buscar(Long id) {
        return toResponse(getTurma(id));
    }

    @Transactional
    public TurmaDtos.TurmaResponse criar(TurmaDtos.TurmaRequest request) {
        Materia materia = materiaRepository.findById(request.materiaId())
                .orElseThrow(() -> new IllegalArgumentException("Matéria não encontrada"));
        Professor professor = professorRepository.findById(request.professorId())
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado"));
        Turma turma = turmaRepository.save(Turma.builder()
                .materia(materia)
                .professor(professor)
                .semestre(request.semestre())
                .ano(request.ano())
                .build());
        return toResponse(turma);
    }

    @Transactional
    public TurmaDtos.TurmaResponse atualizar(Long id, TurmaDtos.TurmaRequest request) {
        Turma turma = getTurma(id);
        Materia materia = materiaRepository.findById(request.materiaId())
                .orElseThrow(() -> new IllegalArgumentException("Matéria não encontrada"));
        Professor professor = professorRepository.findById(request.professorId())
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado"));
        turma.setMateria(materia);
        turma.setProfessor(professor);
        turma.setSemestre(request.semestre());
        turma.setAno(request.ano());
        return toResponse(turma);
    }

    @Transactional
    public void remover(Long id) {
        turmaRepository.delete(getTurma(id));
    }

    private Turma getTurma(Long id) {
        return turmaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada"));
    }

    private TurmaDtos.TurmaResponse toResponse(Turma turma) {
        return new TurmaDtos.TurmaResponse(
                turma.getId(),
                turma.getMateria().getId(),
                turma.getMateria().getCodigo(),
                turma.getMateria().getNome(),
                turma.getMateria().getCurso().getId(),
                turma.getMateria().getCurso().getNome(),
                turma.getProfessor().getId(),
                turma.getProfessor().getUsuario().getNome(),
                turma.getSemestre(),
                turma.getAno()
        );
    }
}
