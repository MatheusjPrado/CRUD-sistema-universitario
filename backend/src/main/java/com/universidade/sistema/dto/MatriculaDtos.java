package com.universidade.sistema.dto;

import jakarta.validation.constraints.NotNull;

public class MatriculaDtos {

    public record MatriculaRequest(
            @NotNull Long alunoId,
            @NotNull Long turmaId
    ) {}

    public record MatriculaMateriaRequest(
            @NotNull Long alunoId,
            @NotNull Long materiaId
    ) {}

    public record NotaRequest(
            @NotNull Double nota
    ) {}

    public record MatriculaResponse(
            Long id,
            Long alunoId,
            String alunoNome,
            String matricula,
            Long turmaId,
            Long materiaId,
            String materiaNome,
            String cursoNome,
            String semestre,
            Integer ano,
            Double nota
    ) {}
}
