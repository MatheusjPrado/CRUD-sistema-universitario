package com.universidade.sistema.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "materias", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"curso_id", "codigo"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String nome;

    private Integer cargaHoraria;

    private Integer semestreSugerido;
}
