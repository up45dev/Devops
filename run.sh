#!/bin/bash

# Script para execução do Sistema de Gerenciamento de Projetos e Tarefas
# Autor: MiniMax Agent

echo "========================================"
echo "Sistema de Gerenciamento de Projetos"
echo "========================================"
echo ""

# Função para mostrar ajuda
show_help() {
    echo "Uso: $0 [COMANDO]"
    echo ""
    echo "Comandos disponíveis:"
    echo "  setup       - Configuração inicial do projeto"
    echo "  dev         - Executa em modo de desenvolvimento (H2)"
    echo "  prod        - Compila e executa em modo de produção (MySQL)"
    echo "  test        - Executa todos os testes"
    echo "  clean       - Limpa o projeto"
    echo "  docs        - Abre a documentação da API"
    echo "  db-setup    - Configura o banco MySQL"
    echo "  help        - Mostra esta ajuda"
    echo ""
}

# Função para verificar se o Maven está instalado
check_maven() {
    if ! command -v mvn &> /dev/null; then
        echo "[ERRO] Maven não encontrado. Por favor, instale o Apache Maven."
        exit 1
    fi
    echo "[INFO] Maven encontrado: $(mvn --version | head -n 1)"
}

# Função para verificar Java
check_java() {
    if ! command -v java &> /dev/null; then
        echo "[ERRO] Java não encontrado. Por favor, instale o Java 17 ou superior."
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        echo "[ERRO] Java 17 ou superior é requerido. Versão atual: $JAVA_VERSION"
        exit 1
    fi
    
    echo "[INFO] Java encontrado: $(java -version 2>&1 | head -n 1)"
}

# Função de setup
setup() {
    echo "[INFO] Verificando pré-requisitos..."
    check_java
    check_maven
    
    echo "[INFO] Compilando o projeto..."
    mvn clean compile
    
    if [ $? -eq 0 ]; then
        echo "[SUCESSO] Setup concluído com sucesso!"
        echo ""
        echo "Próximos passos:"
        echo "  ./run.sh dev      - Para executar em modo desenvolvimento"
        echo "  ./run.sh test     - Para executar os testes"
    else
        echo "[ERRO] Falha na compilação. Verifique os logs acima."
        exit 1
    fi
}

# Função para modo desenvolvimento
dev() {
    echo "[INFO] Iniciando em modo de desenvolvimento..."
    echo "[INFO] Banco de dados: H2 (em memória)"
    echo "[INFO] URL da API: http://localhost:8080/api"
    echo "[INFO] Swagger: http://localhost:8080/swagger-ui.html"
    echo "[INFO] H2 Console: http://localhost:8080/h2-console"
    echo ""
    
    mvn spring-boot:run -Dspring-boot.run.profiles=dev
}

# Função para modo produção
prod() {
    echo "[INFO] Compilando para produção..."
    mvn clean package -DskipTests
    
    if [ $? -eq 0 ]; then
        echo "[INFO] Iniciando em modo de produção..."
        echo "[INFO] Banco de dados: MySQL"
        echo "[INFO] URL da API: http://localhost:8080/api"
        echo ""
        
        java -jar target/projeto-management-1.0.0.jar
    else
        echo "[ERRO] Falha na compilação."
        exit 1
    fi
}

# Função para executar testes
test() {
    echo "[INFO] Executando testes..."
    mvn clean test
    
    if [ $? -eq 0 ]; then
        echo "[SUCESSO] Todos os testes passaram!"
    else
        echo "[ERRO] Alguns testes falharam."
        exit 1
    fi
}

# Função para limpeza
clean() {
    echo "[INFO] Limpando o projeto..."
    mvn clean
    echo "[SUCESSO] Projeto limpo!"
}

# Função para abrir documentação
docs() {
    echo "[INFO] Abrindo documentação da API..."
    echo "[INFO] Certifique-se de que a aplicação está rodando primeiro."
    echo ""
    echo "URLs da documentação:"
    echo "  Swagger UI: http://localhost:8080/swagger-ui.html"
    echo "  OpenAPI JSON: http://localhost:8080/api-docs"
    echo ""
    
    # Tenta abrir no navegador (funciona no macOS e algumas distribuições Linux)
    if command -v open &> /dev/null; then
        open "http://localhost:8080/swagger-ui.html"
    elif command -v xdg-open &> /dev/null; then
        xdg-open "http://localhost:8080/swagger-ui.html"
    else
        echo "[INFO] Abra manualmente no navegador: http://localhost:8080/swagger-ui.html"
    fi
}

# Função para setup do MySQL
db_setup() {
    echo "[INFO] Configurando banco de dados MySQL..."
    echo ""
    echo "Execute os seguintes comandos no MySQL:"
    echo ""
    echo "CREATE DATABASE projeto_management;"
    echo "CREATE USER 'projeto_user'@'localhost' IDENTIFIED BY 'projeto_pass';"
    echo "GRANT ALL PRIVILEGES ON projeto_management.* TO 'projeto_user'@'localhost';"
    echo "FLUSH PRIVILEGES;"
    echo ""
    echo "Depois execute:"
    echo "mysql -u projeto_user -p projeto_management < database/schema.sql"
    echo ""
    echo "Ajuste as configurações em src/main/resources/application.properties se necessário."
}

# Processamento dos argumentos
case "$1" in
    setup)
        setup
        ;;
    dev)
        check_java
        check_maven
        dev
        ;;
    prod)
        check_java
        check_maven
        prod
        ;;
    test)
        check_maven
        test
        ;;
    clean)
        check_maven
        clean
        ;;
    docs)
        docs
        ;;
    db-setup)
        db_setup
        ;;
    help|--help|-h)
        show_help
        ;;
    "")
        echo "[ERRO] Comando não especificado."
        echo ""
        show_help
        exit 1
        ;;
    *)
        echo "[ERRO] Comando desconhecido: $1"
        echo ""
        show_help
        exit 1
        ;;
esac
