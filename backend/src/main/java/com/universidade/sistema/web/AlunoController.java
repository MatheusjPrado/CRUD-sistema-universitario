package com.universidade.sistema.web;

import com.universidade.sistema.dto.AlunoDtos;
import com.universidade.sistema.service.AlunoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
@RequiredArgsConstructor
public class AlunoController {

    private final AlunoService alunoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public List<AlunoDtos.AlunoResponse> listar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long cursoId,
            @RequestParam(required = false) Boolean semCurso
    ) {
        return alunoService.listar(q, cursoId, semCurso);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','ALUNO')")
    public AlunoDtos.AlunoResponse buscar(@PathVariable Long id) {
        return alunoService.buscar(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public AlunoDtos.AlunoResponse criar(@Valid @RequestBody AlunoDtos.AlunoRequest request) {
        return alunoService.criar(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AlunoDtos.AlunoResponse atualizar(@PathVariable Long id, @Valid @RequestBody AlunoDtos.AlunoRequest request) {
        return alunoService.atualizar(id, request);
    }

    @PostMapping("/{id}/matricular-curso")
    @PreAuthorize("hasRole('ADMIN')")
    public AlunoDtos.AlunoResponse matricularCurso(
            @PathVariable Long id,
            @Valid @RequestBody AlunoDtos.MatricularCursoRequest request
    ) {
        return alunoService.matricularEmCurso(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void remover(@PathVariable Long id) {
        alunoService.remover(id);
    }
}
