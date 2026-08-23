package com.universidade.sistema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MateriaDtos {

    public record MateriaRequest(
            @NotNull Long cursoId,
            @NotBlank String codigo,
            @NotBlank String nome,
            Integer cargaHoraria,
            Integer semestreSugerido
    ) {}

    public record MateriaResponse(
            Long id,
            Long cursoId,
            String cursoNome,
            String codigo,
            String nome,
            Integer cargaHoraria,
            Integer semestreSugerido
    ) {}
}
