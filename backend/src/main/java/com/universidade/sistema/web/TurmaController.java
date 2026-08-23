package com.universidade.sistema.web;

import com.universidade.sistema.dto.TurmaDtos;
import com.universidade.sistema.service.TurmaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turmas")
@RequiredArgsConstructor
public class TurmaController {

    private final TurmaService turmaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','ALUNO')")
    public List<TurmaDtos.TurmaResponse> listar(@RequestParam(required = false) Long professorId) {
        if (professorId != null) {
            return turmaService.listarPorProfessor(professorId);
        }
        return turmaService.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','ALUNO')")
    public TurmaDtos.TurmaResponse buscar(@PathVariable Long id) {
        return turmaService.buscar(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public TurmaDtos.TurmaResponse criar(@Valid @RequestBody TurmaDtos.TurmaRequest request) {
        return turmaService.criar(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TurmaDtos.TurmaResponse atualizar(@PathVariable Long id, @Valid @RequestBody TurmaDtos.TurmaRequest request) {
        return turmaService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void remover(@PathVariable Long id) {
        turmaService.remover(id);
    }
}
