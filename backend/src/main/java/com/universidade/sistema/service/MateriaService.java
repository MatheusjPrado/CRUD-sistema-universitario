package com.universidade.sistema.service;

import com.universidade.sistema.domain.Curso;
import com.universidade.sistema.domain.Materia;
import com.universidade.sistema.dto.MateriaDtos;
import com.universidade.sistema.repository.CursoRepository;
import com.universidade.sistema.repository.MateriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MateriaService {

    private final MateriaRepository materiaRepository;
    private final CursoRepository cursoRepository;

    public List<MateriaDtos.MateriaResponse> listar(Long cursoId) {
        List<Materia> materias = cursoId == null
                ? materiaRepository.findAll()
                : materiaRepository.findByCursoIdOrderBySemestreSugeridoAscNomeAsc(cursoId);
        return materias.stream().map(this::toResponse).toList();
    }

    public MateriaDtos.MateriaResponse buscar(Long id) {
        return toResponse(getMateria(id));
    }

    @Transactional
    public MateriaDtos.MateriaResponse criar(MateriaDtos.MateriaRequest request) {
        Curso curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));
        if (materiaRepository.existsByCursoIdAndCodigo(curso.getId(), request.codigo().trim().toUpperCase())) {
            throw new IllegalArgumentException("Já existe matéria com este código neste curso");
        }
        Materia materia = materiaRepository.save(Materia.builder()
                .curso(curso)
                .codigo(request.codigo().trim().toUpperCase())
                .nome(request.nome().trim())
                .cargaHoraria(request.cargaHoraria() != null ? request.cargaHoraria() : 60)
                .semestreSugerido(request.semestreSugerido() != null ? request.semestreSugerido() : 1)
                .build());
        return toResponse(materia);
    }

    public Materia getMateria(Long id) {
        return materiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Matéria não encontrada"));
    }

    private MateriaDtos.MateriaResponse toResponse(Materia materia) {
        return new MateriaDtos.MateriaResponse(
                materia.getId(),
                materia.getCurso().getId(),
                materia.getCurso().getNome(),
                materia.getCodigo(),
                materia.getNome(),
                materia.getCargaHoraria(),
                materia.getSemestreSugerido()
        );
    }
}
