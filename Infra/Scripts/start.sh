#!/bin/bash
set -e

echo "Subindo aplicação (Front + Back + DB PostgreSQL)"

COMPOSE_FILE="Infra/docker/docker-compose.yml"

if docker compose version >/dev/null 2>&1; then
  docker compose -f "$COMPOSE_FILE" up --build
elif command -v docker-compose >/dev/null 2>&1; then
  docker-compose -f "$COMPOSE_FILE" up --build
else
  echo "Erro: Docker Compose não encontrado."
  echo "Instale o Docker Desktop (macOS) ou o plugin docker-compose."
  exit 1
fi