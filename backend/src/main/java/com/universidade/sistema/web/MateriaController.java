package com.universidade.sistema.web;

import com.universidade.sistema.dto.MateriaDtos;
import com.universidade.sistema.service.MateriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materias")
@RequiredArgsConstructor
public class MateriaController {

    private final MateriaService materiaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','ALUNO')")
    public List<MateriaDtos.MateriaResponse> listar(@RequestParam(required = false) Long cursoId) {
        return materiaService.listar(cursoId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','ALUNO')")
    public MateriaDtos.MateriaResponse buscar(@PathVariable Long id) {
        return materiaService.buscar(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MateriaDtos.MateriaResponse criar(@Valid @RequestBody MateriaDtos.MateriaRequest request) {
        return materiaService.criar(request);
    }
}
