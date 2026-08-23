package com.universidade.sistema.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ProfessorDtos {

    public record ProfessorRequest(
            @NotBlank String nome,
            @NotBlank @Email String email,
            String senha,
            String departamento
    ) {}

    public record ProfessorResponse(
            Long id,
            Long usuarioId,
            String nome,
            String email,
            String departamento,
            boolean ativo
    ) {}
}
