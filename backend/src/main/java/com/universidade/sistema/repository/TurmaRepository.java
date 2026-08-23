package com.universidade.sistema.repository;

import com.universidade.sistema.domain.Turma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TurmaRepository extends JpaRepository<Turma, Long> {
    List<Turma> findByProfessorId(Long professorId);
    List<Turma> findByMateriaId(Long materiaId);
    List<Turma> findByMateriaCursoId(Long cursoId);
}
