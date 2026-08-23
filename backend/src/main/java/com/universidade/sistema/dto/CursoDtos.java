package com.universidade.sistema.dto;

public class CursoDtos {

    public record CursoResponse(
            Long id,
            String codigo,
            String nome
    ) {}
}
