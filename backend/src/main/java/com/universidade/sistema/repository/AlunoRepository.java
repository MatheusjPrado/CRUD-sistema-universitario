package com.universidade.sistema.repository;

import com.universidade.sistema.domain.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    Optional<Aluno> findByUsuarioId(Long usuarioId);
    boolean existsByMatricula(String matricula);
    List<Aluno> findByCursoId(Long cursoId);
    List<Aluno> findByCursoIsNull();

    @Query("select max(a.matricula) from Aluno a where a.matricula like concat(?1, '%')")
    Optional<String> findMaxMatriculaByPrefix(String prefix);

    @Query("""
            select a from Aluno a
            where lower(a.usuario.nome) like lower(concat('%', :q, '%'))
               or lower(a.matricula) like lower(concat('%', :q, '%'))
            """)
    List<Aluno> search(@Param("q") String q);
}
