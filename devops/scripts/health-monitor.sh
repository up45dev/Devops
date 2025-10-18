#!/bin/bash

# Script de monitoramento e health check da aplicação
# Gestão de Tarefas - Health Monitor

set -e

# Configurações
BACKEND_URL="http://localhost:8080"
FRONTEND_URL="http://localhost"
PROMETHEUS_URL="http://localhost:9090"
GRAFANA_URL="http://localhost:3000"
SLACK_WEBHOOK_URL="${SLACK_WEBHOOK_URL:-}"
EMAIL_NOTIFICATION="${EMAIL_NOTIFICATION:-admin@example.com}"

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Funções de log
log_info() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')] [INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] [SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] [WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] [ERROR]${NC} $1"
}

# Função para enviar notificação Slack
send_slack_notification() {
    local message="$1"
    local color="$2"
    
    if [ -n "$SLACK_WEBHOOK_URL" ]; then
        curl -X POST -H 'Content-type: application/json' \
            --data "{\"attachments\":[{\"color\":\"$color\",\"text\":\"$message\"}]}" \
            "$SLACK_WEBHOOK_URL" &>/dev/null
    fi
}

# Função para verificar saúde de um serviço
check_service_health() {
    local service_name="$1"
    local url="$2"
    local expected_status="$3"
    
    log_info "Verificando $service_name..."
    
    response=$(curl -s -w "%{http_code}" -o /dev/null "$url" 2>/dev/null || echo "000")
    
    if [ "$response" = "$expected_status" ]; then
        log_success "$service_name está saudável (HTTP $response)"
        return 0
    else
        log_error "$service_name com problemas (HTTP $response)"
        send_slack_notification "🚨 $service_name com problemas (HTTP $response)" "danger"
        return 1
    fi
}

# Função para verificar métricas
check_metrics() {
    log_info "Verificando métricas da aplicação..."
    
    # Verificar métricas do Prometheus
    metrics_response=$(curl -s "$PROMETHEUS_URL/api/v1/query?query=up{job='gestao-tarefas-backend'}" | jq -r '.data.result[0].value[1]' 2>/dev/null || echo "0")
    
    if [ "$metrics_response" = "1" ]; then
        log_success "Métricas da aplicação disponíveis"
        
        # Verificar response time
        response_time=$(curl -s "$PROMETHEUS_URL/api/v1/query?query=http_server_requests_seconds{quantile='0.95',application='gestao-tarefas'}" | jq -r '.data.result[0].value[1]' 2>/dev/null || echo "0")
        
        if (( $(echo "$response_time > 1" | bc -l) )); then
            log_warning "Tempo de resposta elevado: ${response_time}s"
            send_slack_notification "⚠️ Tempo de resposta elevado: ${response_time}s" "warning"
        fi
        
        # Verificar uso de memória
        memory_usage=$(curl -s "$PROMETHEUS_URL/api/v1/query?query=jvm_memory_used_bytes{application='gestao-tarefas',area='heap'}/jvm_memory_max_bytes{application='gestao-tarefas',area='heap'}" | jq -r '.data.result[0].value[1]' 2>/dev/null || echo "0")
        
        if (( $(echo "$memory_usage > 0.85" | bc -l) )); then
            log_warning "Uso de memória elevado: $(echo "$memory_usage * 100" | bc -l | cut -d'.' -f1)%"
            send_slack_notification "⚠️ Uso de memória elevado: $(echo "$memory_usage * 100" | bc -l | cut -d'.' -f1)%" "warning"
        fi
        
    else
        log_error "Métricas da aplicação indisponíveis"
        send_slack_notification "🚨 Métricas da aplicação indisponíveis" "danger"
    fi
}

# Função para verificar containers Docker
check_docker_containers() {
    log_info "Verificando containers Docker..."
    
    # Lista de containers esperados
    expected_containers=("gestao-tarefas-backend" "gestao-tarefas-frontend" "gestao-tarefas-db" "gestao-tarefas-prometheus" "gestao-tarefas-grafana")
    
    for container in "${expected_containers[@]}"; do
        status=$(docker ps --filter "name=$container" --format "{{.Status}}" 2>/dev/null || echo "Not found")
        
        if [[ $status == *"Up"* ]]; then
            log_success "Container $container está executando"
        else
            log_error "Container $container não está executando: $status"
            send_slack_notification "🚨 Container $container não está executando" "danger"
        fi
    done
}

# Função para gerar relatório de saúde
generate_health_report() {
    local timestamp=$(date +'%Y-%m-%d_%H-%M-%S')
    local report_file="/tmp/health_report_$timestamp.json"
    
    log_info "Gerando relatório de saúde..."
    
    cat > "$report_file" <<EOF
{
  "timestamp": "$(date -u +%Y-%m-%dT%H:%M:%SZ)",
  "services": {
    "backend": {
      "url": "$BACKEND_URL/actuator/health",
      "status": "$(curl -s -w "%{http_code}" -o /dev/null "$BACKEND_URL/actuator/health" 2>/dev/null || echo "000")"
    },
    "frontend": {
      "url": "$FRONTEND_URL/health",
      "status": "$(curl -s -w "%{http_code}" -o /dev/null "$FRONTEND_URL/health" 2>/dev/null || echo "000")"
    },
    "prometheus": {
      "url": "$PROMETHEUS_URL/-/healthy",
      "status": "$(curl -s -w "%{http_code}" -o /dev/null "$PROMETHEUS_URL/-/healthy" 2>/dev/null || echo "000")"
    },
    "grafana": {
      "url": "$GRAFANA_URL/api/health",
      "status": "$(curl -s -w "%{http_code}" -o /dev/null "$GRAFANA_URL/api/health" 2>/dev/null || echo "000")"
    }
  }
}
EOF
    
    log_success "Relatório gerado: $report_file"
}

# Função principal
main() {
    echo "🩺 Health Check - Gestão de Tarefas"
    echo "======================================"
    
    local exit_code=0
    
    # Verificar containers Docker
    check_docker_containers
    
    # Verificar serviços
    check_service_health "Backend API" "$BACKEND_URL/actuator/health" "200" || exit_code=1
    check_service_health "Frontend" "$FRONTEND_URL/health" "200" || exit_code=1
    check_service_health "Prometheus" "$PROMETHEUS_URL/-/healthy" "200" || exit_code=1
    check_service_health "Grafana" "$GRAFANA_URL/api/health" "200" || exit_code=1
    
    # Verificar métricas
    check_metrics
    
    # Gerar relatório
    generate_health_report
    
    if [ $exit_code -eq 0 ]; then
        log_success "Todos os serviços estão saudáveis! ✅"
        send_slack_notification "✅ Health check concluído com sucesso - Todos os serviços saudáveis" "good"
    else
        log_error "Alguns serviços apresentam problemas! ❌"
        send_slack_notification "❌ Health check detectou problemas nos serviços" "danger"
    fi
    
    exit $exit_code
}

# Verificar dependências
if ! command -v curl &> /dev/null; then
    log_error "curl não está instalado"
    exit 1
fi

if ! command -v jq &> /dev/null; then
    log_warning "jq não está instalado - algumas verificações serão limitadas"
fi

if ! command -v bc &> /dev/null; then
    log_warning "bc não está instalado - cálculos serão limitados"
fi

# Executar função principal
main "$@"
