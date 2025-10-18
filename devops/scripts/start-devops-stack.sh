#!/bin/bash

# Script para inicializar a stack DevOps completa
# Gestão de Tarefas - DevOps Stack

set -e

echo "🚀 Iniciando Stack DevOps - Gestão de Tarefas"
echo "================================================"

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para log colorido
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar se Docker está instalado
if ! command -v docker &> /dev/null; then
    log_error "Docker não está instalado. Por favor, instale o Docker primeiro."
    exit 1
fi

# Verificar se Docker Compose está instalado
if ! command -v docker-compose &> /dev/null; then
    log_error "Docker Compose não está instalado. Por favor, instale o Docker Compose primeiro."
    exit 1
fi

# Navegar para o diretório DevOps
cd "$(dirname "$0")"

# Parar containers existentes
log_info "Parando containers existentes..."
docker-compose down --volumes --remove-orphans 2>/dev/null || true

# Limpar imagens antigas
log_info "Limpando imagens antigas..."
docker system prune -f

# Construir e iniciar a stack
log_info "Construindo e iniciando a stack DevOps..."
docker-compose up --build -d

# Aguardar os serviços ficarem saudáveis
log_info "Aguardando serviços ficarem saudáveis..."
sleep 30

# Verificar status dos containers
log_info "Verificando status dos containers..."
docker-compose ps

# Função para verificar se um serviço está saudável
check_service() {
    local service_name=$1
    local url=$2
    local max_attempts=30
    local attempt=1
    
    log_info "Verificando $service_name..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s -f "$url" > /dev/null 2>&1; then
            log_success "$service_name está funcionando!"
            return 0
        fi
        
        log_warning "Tentativa $attempt/$max_attempts para $service_name..."
        sleep 5
        ((attempt++))
    done
    
    log_error "$service_name não está respondendo após $max_attempts tentativas."
    return 1
}

# Verificar serviços
log_info "Verificando saúde dos serviços..."

# Backend (aguardar mais tempo por causa da inicialização do Spring Boot)
log_info "Aguardando inicialização do backend (pode levar até 2 minutos)..."
sleep 60
check_service "Backend API" "http://localhost:8080/actuator/health"

# Frontend
check_service "Frontend" "http://localhost/health"

# Prometheus
check_service "Prometheus" "http://localhost:9090/-/healthy"

# Grafana
check_service "Grafana" "http://localhost:3000/api/health"

# Mostrar URLs de acesso
echo ""
log_success "Stack DevOps iniciada com sucesso!"
echo "================================================"
echo -e "${GREEN}🌐 URLs de Acesso:${NC}"
echo "  • Frontend:     http://localhost"
echo "  • Backend API:  http://localhost:8080"
echo "  • Prometheus:   http://localhost:9090"
echo "  • Grafana:      http://localhost:3000 (admin/admin)"
echo "  • PostgreSQL:   localhost:5432 (admin/admin123)"
echo ""
echo -e "${BLUE}📊 Monitoramento:${NC}"
echo "  • Métricas da aplicação disponíveis no Prometheus"
echo "  • Dashboards personalizados no Grafana"
echo "  • Health checks automáticos configurados"
echo ""
echo -e "${YELLOW}💡 Comandos úteis:${NC}"
echo "  • Ver logs:           docker-compose logs -f [serviço]"
echo "  • Parar stack:       docker-compose down"
echo "  • Restart serviço:   docker-compose restart [serviço]"
echo "  • Status containers: docker-compose ps"
echo ""
echo -e "${GREEN}✅ Pronto para uso!${NC}"
