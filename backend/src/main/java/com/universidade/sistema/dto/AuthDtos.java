package com.universidade.sistema.dto;

import com.universidade.sistema.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String senha
    ) {}

    public record LoginResponse(
            String token,
            Long usuarioId,
            String nome,
            String email,
            Role role
    ) {}

    public record MeResponse(
            Long usuarioId,
            String nome,
            String email,
            Role role,
            Long alunoId,
            Long professorId
    ) {}
}
