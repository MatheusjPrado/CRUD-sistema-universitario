package com.universidade.sistema.service;

import com.universidade.sistema.domain.Curso;
import com.universidade.sistema.dto.CursoDtos;
import com.universidade.sistema.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;

    public List<CursoDtos.CursoResponse> listar() {
        return cursoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CursoDtos.CursoResponse buscar(Long id) {
        return toResponse(getCurso(id));
    }

    public Curso getCurso(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));
    }

    private CursoDtos.CursoResponse toResponse(Curso curso) {
        return new CursoDtos.CursoResponse(curso.getId(), curso.getCodigo(), curso.getNome());
    }
}
