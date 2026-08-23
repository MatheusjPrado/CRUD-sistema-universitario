package com.universidade.sistema.web;

import com.universidade.sistema.dto.ProfessorDtos;
import com.universidade.sistema.service.ProfessorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professores")
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService professorService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProfessorDtos.ProfessorResponse> listar() {
        return professorService.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    public ProfessorDtos.ProfessorResponse buscar(@PathVariable Long id) {
        return professorService.buscar(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProfessorDtos.ProfessorResponse criar(@Valid @RequestBody ProfessorDtos.ProfessorRequest request) {
        return professorService.criar(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProfessorDtos.ProfessorResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProfessorDtos.ProfessorRequest request
    ) {
        return professorService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void remover(@PathVariable Long id) {
        professorService.remover(id);
    }
}
