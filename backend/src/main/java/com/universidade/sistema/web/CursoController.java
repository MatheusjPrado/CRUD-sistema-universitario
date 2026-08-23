package com.universidade.sistema.web;

import com.universidade.sistema.dto.CursoDtos;
import com.universidade.sistema.service.CursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','ALUNO')")
    public List<CursoDtos.CursoResponse> listar() {
        return cursoService.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','ALUNO')")
    public CursoDtos.CursoResponse buscar(@PathVariable Long id) {
        return cursoService.buscar(id);
    }
}
