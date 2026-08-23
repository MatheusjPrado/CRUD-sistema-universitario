package com.universidade.sistema.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AlunoDtos {

    public record AlunoRequest(
            @NotBlank String nome,
            @NotBlank @Email String email,
            String senha,
            Long cursoId
    ) {}

    public record MatricularCursoRequest(
            @NotNull Long cursoId
    ) {}

    public record AlunoResponse(
            Long id,
            Long usuarioId,
            String nome,
            String email,
            String matricula,
            Long cursoId,
            String cursoNome,
            boolean ativo
    ) {}
}
