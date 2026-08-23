package com.universidade.sistema.web;

import com.universidade.sistema.dto.MatriculaDtos;
import com.universidade.sistema.service.MatriculaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matriculas")
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService matriculaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','ALUNO')")
    public List<MatriculaDtos.MatriculaResponse> listar(
            @RequestParam(required = false) Long alunoId,
            @RequestParam(required = false) Long turmaId
    ) {
        if (alunoId != null) {
            return matriculaService.listarPorAluno(alunoId);
        }
        if (turmaId != null) {
            return matriculaService.listarPorTurma(turmaId);
        }
        return matriculaService.listar();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MatriculaDtos.MatriculaResponse criar(@Valid @RequestBody MatriculaDtos.MatriculaRequest request) {
        return matriculaService.criar(request);
    }

    @PostMapping("/materia")
    @PreAuthorize("hasRole('ADMIN')")
    public MatriculaDtos.MatriculaResponse matricularMateria(
            @Valid @RequestBody MatriculaDtos.MatriculaMateriaRequest request
    ) {
        return matriculaService.matricularEmMateria(request);
    }

    @PatchMapping("/{id}/nota")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public MatriculaDtos.MatriculaResponse lancarNota(
            @PathVariable Long id,
            @Valid @RequestBody MatriculaDtos.NotaRequest request
    ) {
        return matriculaService.lancarNota(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void remover(@PathVariable Long id) {
        matriculaService.remover(id);
    }
}
