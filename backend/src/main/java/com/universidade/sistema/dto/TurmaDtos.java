package com.universidade.sistema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TurmaDtos {

    public record TurmaRequest(
            @NotNull Long materiaId,
            @NotNull Long professorId,
            @NotBlank String semestre,
            @NotNull Integer ano
    ) {}

    public record TurmaResponse(
            Long id,
            Long materiaId,
            String materiaCodigo,
            String materiaNome,
            Long cursoId,
            String cursoNome,
            Long professorId,
            String professorNome,
            String semestre,
            Integer ano
    ) {}
}
