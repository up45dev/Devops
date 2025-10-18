#!/bin/bash

# Criar diretório de logs
mkdir -p logs

echo "🚀 Task Management Microservices"
echo "================================"
echo ""
echo "📋 Serviços Disponíveis:"
echo "  1. Discovery Service (Eureka) - Port 8761"
echo "  2. Auth Service (OAuth2+JWT) - Port 8080" 
echo "  3. API Gateway - Port 8000"
echo "  4. Projeto Service (HATEOAS) - Port 8081"
echo "  5. Admin UI (Vaadin) - Port 8090"
echo ""
echo "🔧 Para iniciar todos os serviços:"
echo "  mvn clean install -DskipTests"
echo ""
echo "🌐 URLs principais:"
echo "  - Eureka: http://localhost:8761"
echo "  - Admin UI: http://localhost:8090"
echo "  - API Gateway: http://localhost:8000"
echo ""
echo "📚 Documentação:"
echo "  - README.md - Visão geral"
echo "  - MICROSERVICES-ARCHITECTURE-SUMMARY.md - Arquitetura detalhada"
echo "  - OAUTH2-SETUP-GUIDE.md - Configuração OAuth2"
echo ""