package com.universidade.sistema.config;

import com.universidade.sistema.domain.*;
import com.universidade.sistema.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final CursoRepository cursoRepository;
    private final MateriaRepository materiaRepository;
    private final TurmaRepository turmaRepository;
    private final MatriculaRepository matriculaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        Curso cc = cursoRepository.save(Curso.builder().codigo("CC").nome("Ciência da Computação").build());
        Curso ee = cursoRepository.save(Curso.builder().codigo("EE").nome("Engenharia Elétrica").build());
        Curso em = cursoRepository.save(Curso.builder().codigo("EM").nome("Engenharia Mecânica").build());

        seedMaterias(cc, List.of(
                m("ALG1", "Algoritmos e Programação", 80, 1),
                m("POO", "Programação Orientada a Objetos", 80, 2),
                m("BD1", "Banco de Dados", 60, 3),
                m("ES2", "Engenharia de Software II", 60, 4),
                m("REDES", "Redes de Computadores", 60, 5),
                m("SO", "Sistemas Operacionais", 60, 5)
        ));

        seedMaterias(ee, List.of(
                m("CIR1", "Circuitos Elétricos I", 80, 1),
                m("DIG", "Sistemas Digitais", 60, 2),
                m("ELET", "Eletromagnetismo", 60, 3),
                m("ELETRON", "Eletrônica Analógica", 60, 4),
                m("CONT", "Controle e Automação", 60, 5),
                m("POT", "Sistemas de Potência", 60, 6)
        ));

        seedMaterias(em, List.of(
                m("MEC1", "Mecânica dos Sólidos", 80, 1),
                m("MAT", "Materiais de Engenharia", 60, 2),
                m("TERMO", "Termodinâmica", 60, 3),
                m("FLUID", "Mecânica dos Fluidos", 60, 4),
                m("MAQ", "Elementos de Máquinas", 60, 5),
                m("CNC", "Processos de Fabricação", 60, 6)
        ));

        Usuario admin = usuarioRepository.save(Usuario.builder()
                .nome("Administrador")
                .email("admin@uni.local")
                .senha(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .ativo(true)
                .build());

        Usuario profUser = usuarioRepository.save(Usuario.builder()
                .nome("Ana Professora")
                .email("prof@uni.local")
                .senha(passwordEncoder.encode("prof123"))
                .role(Role.PROFESSOR)
                .ativo(true)
                .build());

        Usuario alunoUser = usuarioRepository.save(Usuario.builder()
                .nome("Carlos Aluno")
                .email("aluno@uni.local")
                .senha(passwordEncoder.encode("aluno123"))
                .role(Role.ALUNO)
                .ativo(true)
                .build());

        Professor professor = professorRepository.save(Professor.builder()
                .usuario(profUser)
                .departamento("Computação")
                .build());

        Aluno aluno = alunoRepository.save(Aluno.builder()
                .usuario(alunoUser)
                .matricula(Year.now().getValue() + "0001")
                .curso(cc)
                .build());

        Usuario semCursoUser = usuarioRepository.save(Usuario.builder()
                .nome("Bruno Sem Curso")
                .email("bruno@uni.local")
                .senha(passwordEncoder.encode("aluno123"))
                .role(Role.ALUNO)
                .ativo(true)
                .build());

        alunoRepository.save(Aluno.builder()
                .usuario(semCursoUser)
                .matricula(Year.now().getValue() + "0002")
                .curso(null)
                .build());

        Materia es2 = materiaRepository.findByCursoIdOrderBySemestreSugeridoAscNomeAsc(cc.getId()).stream()
                .filter(m -> "ES2".equals(m.getCodigo()))
                .findFirst()
                .orElseThrow();

        Turma turma = turmaRepository.save(Turma.builder()
                .materia(es2)
                .professor(professor)
                .semestre("1")
                .ano(Year.now().getValue())
                .build());

        matriculaRepository.save(Matricula.builder()
                .aluno(aluno)
                .turma(turma)
                .nota(8.5)
                .build());

        if (admin.getId() == null) {
            throw new IllegalStateException("Falha ao criar admin");
        }
    }

    private record MateriaSeed(String codigo, String nome, int carga, int semestre) {}

    private MateriaSeed m(String codigo, String nome, int carga, int semestre) {
        return new MateriaSeed(codigo, nome, carga, semestre);
    }

    private void seedMaterias(Curso curso, List<MateriaSeed> seeds) {
        for (MateriaSeed seed : seeds) {
            materiaRepository.save(Materia.builder()
                    .curso(curso)
                    .codigo(seed.codigo())
                    .nome(seed.nome())
                    .cargaHoraria(seed.carga())
                    .semestreSugerido(seed.semestre())
                    .build());
        }
    }
}
