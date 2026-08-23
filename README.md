# UniSistema — Sistema Universitário (v1)

Stack: React + Spring Boot + PostgreSQL + Docker

## Subir com Docker (recomendado)

```bash
docker compose up --build
```

- Frontend: http://localhost:5173
- Backend: http://localhost:8080
- Postgres: localhost:5432

## Desenvolvimento local

1. Subir só o banco:

```bash
docker compose up db -d
```

2. Backend:

```bash
cd backend
mvn spring-boot:run
```

3. Frontend:

```bash
cd frontend
npm install
npm run dev
```

## Logins de teste

| Papel | E-mail | Senha |
|-------|--------|-------|
| Admin | admin@uni.local | admin123 |
| Professor | prof@uni.local | prof123 |
| Aluno | aluno@uni.local | aluno123 |

## O que o v1 faz

- Login JWT com roles ADMIN / PROFESSOR / ALUNO
- Admin: CRUD de alunos, professores, cursos, turmas e matrículas
- Professor: vê turmas e lança notas
- Aluno: vê disciplinas e notas
