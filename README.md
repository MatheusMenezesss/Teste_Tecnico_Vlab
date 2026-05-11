# Tasktrack / Vlab

## Como subir o projeto

1. Garanta que Docker e Docker Compose estejam instalados.
2. Execute o script de inicialização:

```bash
bash Infra/Scripts/start.sh
```

## Serviços expostos

- Frontend: http://localhost:5173
- Backend: http://localhost:8080
- PostgreSQL: localhost:5432

## Observações

- O frontend roda com Vite em `0.0.0.0`, então fica acessível pelo navegador.
- O backend usa o banco PostgreSQL do compose por variáveis de ambiente.
