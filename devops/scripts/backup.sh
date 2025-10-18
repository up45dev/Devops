#!/bin/bash

# Script de backup automatizado
# Gestão de Tarefas - Backup System

set -e

# Configurações
BACKUP_DIR="/backup/gestao-tarefas"
DB_CONTAINER="gestao-tarefas-db"
DB_NAME="gestao_tarefas"
DB_USER="admin"
RETENTION_DAYS=30
S3_BUCKET="${S3_BUCKET:-}"
SLACK_WEBHOOK_URL="${SLACK_WEBHOOK_URL:-}"

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

# Função para criar backup do banco de dados
backup_database() {
    local timestamp=$(date +'%Y%m%d_%H%M%S')
    local backup_file="$BACKUP_DIR/database/gestao_tarefas_$timestamp.sql"
    
    log_info "Iniciando backup do banco de dados..."
    
    # Criar diretório se não existir
    mkdir -p "$(dirname "$backup_file")"
    
    # Realizar backup
    if docker exec "$DB_CONTAINER" pg_dump -U "$DB_USER" "$DB_NAME" > "$backup_file"; then
        
        # Comprimir backup
        gzip "$backup_file"
        backup_file="$backup_file.gz"
        
        local backup_size=$(ls -lh "$backup_file" | awk '{print $5}')
        log_success "Backup do banco criado: $backup_file ($backup_size)"
        
        # Upload para S3 se configurado
        if [ -n "$S3_BUCKET" ] && command -v aws &> /dev/null; then
            log_info "Uploading backup to S3..."
            if aws s3 cp "$backup_file" "s3://$S3_BUCKET/database/"; then
                log_success "Backup enviado para S3"
            else
                log_error "Falha ao enviar backup para S3"
            fi
        fi
        
        send_slack_notification "✅ Backup do banco de dados criado com sucesso ($backup_size)" "good"
        return 0
    else
        log_error "Falha ao criar backup do banco de dados"
        send_slack_notification "❌ Falha ao criar backup do banco de dados" "danger"
        return 1
    fi
}

# Função para backup dos volumes Docker
backup_volumes() {
    local timestamp=$(date +'%Y%m%d_%H%M%S')
    local volumes_backup_dir="$BACKUP_DIR/volumes/$timestamp"
    
    log_info "Iniciando backup dos volumes Docker..."
    
    mkdir -p "$volumes_backup_dir"
    
    # Lista de volumes para backup
    local volumes=("devops_postgres_data" "devops_prometheus_data" "devops_grafana_data")
    
    for volume in "${volumes[@]}"; do
        log_info "Backup do volume: $volume"
        
        if docker run --rm -v "$volume:/data" -v "$volumes_backup_dir:/backup" alpine tar czf "/backup/$volume.tar.gz" -C /data .; then
            local backup_size=$(ls -lh "$volumes_backup_dir/$volume.tar.gz" | awk '{print $5}')
            log_success "Volume $volume backup criado ($backup_size)"
        else
            log_error "Falha ao criar backup do volume $volume"
        fi
    done
    
    send_slack_notification "✅ Backup dos volumes Docker concluído" "good"
}

# Função para limpeza de backups antigos
cleanup_old_backups() {
    log_info "Limpando backups antigos (> $RETENTION_DAYS dias)..."
    
    # Remover backups do banco antigos
    find "$BACKUP_DIR/database" -name "*.sql.gz" -mtime +$RETENTION_DAYS -delete 2>/dev/null || true
    
    # Remover backups de volumes antigos
    find "$BACKUP_DIR/volumes" -maxdepth 1 -type d -mtime +$RETENTION_DAYS -exec rm -rf {} \; 2>/dev/null || true
    
    log_success "Limpeza de backups antigos concluída"
}

# Função para verificar espaço em disco
check_disk_space() {
    local backup_dir_parent=$(dirname "$BACKUP_DIR")
    local available_space=$(df "$backup_dir_parent" | awk 'NR==2 {print $4}')
    local available_gb=$((available_space / 1024 / 1024))
    
    log_info "Espaço disponível para backup: ${available_gb}GB"
    
    if [ $available_gb -lt 5 ]; then
        log_warning "Pouco espaço disponível para backup (${available_gb}GB)"
        send_slack_notification "⚠️ Pouco espaço disponível para backup (${available_gb}GB)" "warning"
    fi
}

# Função principal
main() {
    echo "💾 Sistema de Backup - Gestão de Tarefas"
    echo "=========================================="
    
    local start_time=$(date +%s)
    
    # Verificar espaço em disco
    check_disk_space
    
    # Realizar backups
    backup_database
    backup_volumes
    
    # Limpeza
    cleanup_old_backups
    
    local end_time=$(date +%s)
    local duration=$((end_time - start_time))
    
    log_success "Backup concluído em ${duration}s"
    
    # Relatório final
    local total_backups=$(find "$BACKUP_DIR" -type f | wc -l)
    local total_size=$(du -sh "$BACKUP_DIR" | cut -f1)
    
    log_info "Total de backups: $total_backups arquivos"
    log_info "Tamanho total: $total_size"
    
    send_slack_notification "✅ Backup automatizado concluído - $total_backups arquivos ($total_size) em ${duration}s" "good"
}

# Verificar se Docker está disponível
if ! command -v docker &> /dev/null; then
    log_error "Docker não está instalado"
    exit 1
fi

# Verificar se container do banco existe
if ! docker ps --format "{{.Names}}" | grep -q "^$DB_CONTAINER$"; then
    log_error "Container do banco ($DB_CONTAINER) não encontrado"
    exit 1
fi

# Executar função principal
main "$@"
