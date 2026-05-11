# Tasktrack / Vlab

## Relatorio do projeto

Tasktrack/Vlab e uma aplicacao full-stack para gerenciamento de cursos e aulas com autenticacao JWT. O backend foi construindo em Spring Boot e expoe endpoints REST para login, cadastro, listagem e manutencao de cursos e aulas. O frontend, feito em React com Vite, consome a API, protege rotas autenticadas e organiza a experiencia em telas de login, home, listagem de cursos e detalhe do curso.

### O que o projeto entrega

- Autenticacao com login e cadastro de usuario, retornando token JWT para acesso as rotas protegidas.
- Protecao de acesso no frontend e no backend, com redirecionamento para login quando nao ha token valido.
- CRUD de cursos com validacao de autoria: apenas o criador pode editar ou remover o proprio curso.
- CRUD de aulas vinculadas ao curso, com status `DRAFT` ou `PUBLISHED` e suporte a video URL.
- Seed inicial com usuarios, cursos e aulas a partir de arquivos JSON para facilitar a avaliacao do desafio.
- Ambiente completo com Docker para subir banco, backend e frontend em uma unica rotina.

### Aderencia ao desafio

- Separacao clara entre camadas de controller, service, repository, domain e security no backend.
- Uso de DTOs para controlar entrada e saida de dados sensiveis.
- Persistencia em PostgreSQL via JPA/Hibernate.
- Consumo da API no frontend por uma camada unica de Axios com injecao automatica do token.
- Navegacao protegida com React Router e componentes de tela dedicados para os fluxos principais do usuario.

### API externa:

- O projeto usa a API `randomuser.me` apenas no seed inicial de usuarios.
- Quando o arquivo `BackEnd/seed-data/users.json` ainda nao existe ou esta vazio, o backend busca 50 perfis na API e monta usuarios locais a partir do nome retornado.
- O sistema gera emails estaveis no formato `usuarioXX@vlab.local` e usa a senha padrao `123456` para facilitar os testes.
- Se a API externa estiver indisponivel, o seed usa um fallback local e continua a subida do projeto normalmente.

### Tecnologias utilizadas

- Backend: Java, Spring Boot, Spring Web, Spring Security, JWT, Spring Data JPA, Bean Validation.
- Banco: PostgreSQL.
- Frontend: React, Vite, React Router, Axios.
- Infra: Docker, Docker Compose e script de inicializacao.

### Estrutura principal

- `BackEnd/src/main/java/br/ufpe/tasktrack/controller`: rotas REST da aplicacao.
- `BackEnd/src/main/java/br/ufpe/tasktrack/service`: regras de negocio e autenticacao.
- `BackEnd/src/main/java/br/ufpe/tasktrack/security`: JWT e configuracao de seguranca.
- `BackEnd/src/main/java/br/ufpe/tasktrack/domain`: entidades do dominio.
- `FrontEnd/VlabFront/src/pages`: telas principais da interface.
- `FrontEnd/VlabFront/src/services`: cliente HTTP e helpers de autenticacao.

## Como subir o projeto

1. Garanta que Docker e Docker Compose estejam instalados.
2. Execute o script de inicializacao:

```bash

sudo bash Infra/Scripts/start.sh
```

## Servicos expostos

- Frontend: http://localhost:5173
- Backend: http://localhost:8080
- PostgreSQL: localhost:5432

## Seed inicial

- `BackEnd/seed-data/users.json` e preenchido automaticamente na primeira subida com 50 usuarios.
- A senha padrao dos usuarios gerados e `123456`.
- `BackEnd/seed-data/courses.json` e `BackEnd/seed-data/lessons.json` podem ser editados manualmente.
- Os cursos usam `creatorEmail` e as aulas usam `courseName` para facilitar a manutencao por quem estiver testando.

## Observacoes

- O frontend roda com Vite em `0.0.0.0`, entao fica acessivel pelo navegador.
- O backend usa o banco PostgreSQL do compose por variaveis de ambiente.
