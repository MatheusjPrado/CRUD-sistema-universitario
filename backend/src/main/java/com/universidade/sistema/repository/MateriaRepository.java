package com.universidade.sistema.repository;

import com.universidade.sistema.domain.Materia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MateriaRepository extends JpaRepository<Materia, Long> {
    List<Materia> findByCursoIdOrderBySemestreSugeridoAscNomeAsc(Long cursoId);
    boolean existsByCursoIdAndCodigo(Long cursoId, String codigo);
}
