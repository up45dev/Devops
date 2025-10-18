#!/bin/bash

# Script para parar todos os microserviços

echo "🛑 Parando Task Management Microservices..."

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

# Criar diretório de logs se não existir
mkdir -p logs

# Array com serviços
services=(
    "discovery-service"
    "auth-service"
    "api-gateway"
    "projeto-service"
    "admin-ui-service"
)

# Parar cada serviço
for service in "${services[@]}"; do
    if [ -f "logs/$service.pid" ]; then
        pid=$(cat logs/$service.pid)
        if ps -p $pid > /dev/null; then
            print_status "Parando $service (PID: $pid)..."
            kill $pid
            print_success "$service parado"
        else
            print_status "$service já estava parado"
        fi
        rm -f logs/$service.pid
    else
        print_status "PID file para $service não encontrado"
    fi
done

print_success "Todos os microserviços foram parados!"
