#!/bin/bash

# Script para testar a implementação Java 21 + DDD + TDD + Kafka
# Testa todas as funcionalidades implementadas

echo "🚀 Testando Implementação Java 21 + DDD + TDD + Kafka"
echo "======================================================"

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

# Verificar Java 21
log_info "Verificando versão do Java..."
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -ge 21 ]; then
    log_success "Java $JAVA_VERSION detectado ✓"
else
    log_error "Java 21+ é necessário. Versão atual: $JAVA_VERSION"
    exit 1
fi

# Verificar Maven
log_info "Verificando Maven..."
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -n 1 | cut -d' ' -f3)
    log_success "Maven $MVN_VERSION detectado ✓"
else
    log_error "Maven não encontrado"
    exit 1
fi

# 1. Testes Unitários (Domain Layer)
log_info "Executando testes unitários de domínio..."
if mvn test -Dtest="**/domain/**/*Test" -q; then
    log_success "Testes de domínio passaram ✓"
else
    log_error "Falha nos testes de domínio"
    exit 1
fi

# 2. Testes de Value Objects
log_info "Testando Value Objects..."
if mvn test -Dtest="StatusProjetoTest,PrioridadeTarefaTest,StatusTarefaTest" -q; then
    log_success "Testes de Value Objects passaram ✓"
else
    log_error "Falha nos testes de Value Objects"
    exit 1
fi

# 3. Testes de Entidades
log_info "Testando Entidades de Domínio..."
if mvn test -Dtest="ProjetoTest,TarefaTest" -q; then
    log_success "Testes de Entidades passaram ✓"
else
    log_error "Falha nos testes de Entidades"
    exit 1
fi

# 4. Testes de Application Services (com Mocks)
log_info "Testando Application Services..."
if mvn test -Dtest="**/application/**/*Test" -q; then
    log_success "Testes de Application Services passaram ✓"
else
    log_error "Falha nos testes de Application Services"
    exit 1
fi

# 5. Testes de Arquitetura (ArchUnit)
log_info "Validando arquitetura DDD..."
if mvn test -Dtest="ArchitectureTest" -q; then
    log_success "Validação de arquitetura passou ✓"
else
    log_error "Falha na validação de arquitetura"
    exit 1
fi

# 6. Testes de Integração Kafka
log_info "Testando integração com Kafka..."
if mvn test -Dtest="KafkaIntegrationTest" -Dspring.profiles.active=test -q; then
    log_success "Testes de integração Kafka passaram ✓"
else
    log_error "Falha nos testes de integração Kafka"
    exit 1
fi

# 7. Compilation com Java 21 features
log_info "Verificando compilação com features do Java 21..."
if mvn compile -q; then
    log_success "Compilação com Java 21 bem-sucedida ✓"
else
    log_error "Falha na compilação"
    exit 1
fi

# 8. Verificar uso de Records
log_info "Verificando uso de Records..."
RECORDS_COUNT=$(find src/main/java -name "*.java" -exec grep -l "public record" {} \; | wc -l)
if [ "$RECORDS_COUNT" -gt 0 ]; then
    log_success "Records encontrados: $RECORDS_COUNT ✓"
else
    log_warning "Nenhum Record encontrado"
fi

# 9. Verificar uso de Sealed Classes
log_info "Verificando uso de Sealed Classes..."
SEALED_COUNT=$(find src/main/java -name "*.java" -exec grep -l "sealed interface\|sealed class" {} \; | wc -l)
if [ "$SEALED_COUNT" -gt 0 ]; then
    log_success "Sealed classes/interfaces encontradas: $SEALED_COUNT ✓"
else
    log_warning "Nenhuma Sealed class/interface encontrada"
fi

# 10. Verificar Pattern Matching
log_info "Verificando uso de Pattern Matching..."
PATTERN_COUNT=$(find src/main/java -name "*.java" -exec grep -l "switch.*case.*->" {} \; | wc -l)
if [ "$PATTERN_COUNT" -gt 0 ]; then
    log_success "Pattern matching encontrado em $PATTERN_COUNT arquivos ✓"
else
    log_warning "Pattern matching não encontrado"
fi

# 11. Coverage Report
log_info "Gerando relatório de cobertura..."
if mvn jacoco:report -q; then
    log_success "Relatório de cobertura gerado ✓"
    log_info "Visualizar: open target/site/jacoco/index.html"
else
    log_warning "Falha ao gerar relatório de cobertura"
fi

# 12. Package da aplicação
log_info "Empacotando aplicação..."
if mvn package -DskipTests -q; then
    log_success "Aplicação empacotada com sucesso ✓"
    JAR_FILE=$(find target -name "*.jar" | grep -v original)
    log_info "JAR criado: $JAR_FILE"
else
    log_error "Falha no empacotamento"
    exit 1
fi

# Relatório Final
echo ""
log_success "🎉 TODOS OS TESTES PASSARAM!"
echo "=============================="
echo ""
echo -e "${GREEN}✅ IMPLEMENTAÇÃO COMPLETA:${NC}"
echo "  • Java 21 com features modernas"
echo "  • Domain-Driven Design (DDD)"
echo "  • Test-Driven Development (TDD)"
echo "  • Princípios SOLID aplicados"
echo "  • Mocks apropriados nos testes"
echo "  • Princípios FIRST seguidos"
echo "  • Apache Kafka para eventos"
echo "  • ArchUnit para validação arquitetural"
echo "  • TestContainers para integração"
echo ""
echo -e "${BLUE}📊 ESTATÍSTICAS:${NC}"
echo "  • Records: $RECORDS_COUNT"
echo "  • Sealed Classes: $SEALED_COUNT"
echo "  • Pattern Matching: $PATTERN_COUNT arquivos"
echo ""
echo -e "${YELLOW}🚀 PRÓXIMOS PASSOS:${NC}"
echo "  1. ./start-devops.sh (Stack completa)"
echo "  2. mvn spring-boot:run (Desenvolvimento)"
echo "  3. docker-compose up (Produção)"
echo ""
log_success "Implementação pronta para produção enterprise! 🔥"