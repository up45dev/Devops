#!/bin/bash

# Script para iniciar todos os microserviços localmente

echo "🚀 Iniciando Task Management Microservices..."

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para imprimir status
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar se o Maven está instalado
if ! command -v mvn &> /dev/null; then
    print_error "Maven não encontrado. Por favor, instale o Maven primeiro."
    exit 1
fi

# Verificar se o Java 21 está instalado
if ! command -v java &> /dev/null; then
    print_error "Java não encontrado. Por favor, instale o Java 21 primeiro."
    exit 1
fi

print_status "Compilando todos os microserviços..."
mvn clean install -DskipTests

if [ $? -ne 0 ]; then
    print_error "Falha na compilação. Verifique os logs acima."
    exit 1
fi

print_success "Compilação concluída com sucesso!"

# Array com serviços na ordem de dependência
services=(
    "discovery-service"
    "auth-service"
    "api-gateway"
    "projeto-service"
    "admin-ui-service"
)

# Iniciar cada serviço
for service in "${services[@]}"; do
    print_status "Iniciando $service..."
    
    cd $service
    nohup mvn spring-boot:run > ../logs/$service.log 2>&1 &
    echo $! > ../logs/$service.pid
    cd ..
    
    print_success "$service iniciado (PID: $(cat logs/$service.pid))"
    
    # Aguardar um pouco antes de iniciar o próximo serviço
    sleep 5
done

print_success "Todos os microserviços foram iniciados!"

echo ""
echo "🌐 URLs dos Serviços:"
echo "  - Eureka Dashboard: http://localhost:8761"
echo "  - API Gateway: http://localhost:8000"
echo "  - Auth Service: http://localhost:8080"
echo "  - Projeto Service: http://localhost:8081"
echo "  - Admin UI (Vaadin): http://localhost:8090"
echo ""
echo "📄 Logs disponíveis em: logs/"
echo "🛑 Para parar os serviços: ./stop-services.sh"
