#!/bin/bash

# Script para execução do Frontend Angular
# Autor: MiniMax Agent

echo "========================================"
echo "Frontend Angular - Gerenciamento de Projetos"
echo "========================================"
echo ""

# Função para mostrar ajuda
show_help() {
    echo "Uso: $0 [COMANDO]"
    echo ""
    echo "Comandos disponíveis:"
    echo "  setup       - Instala dependências do projeto"
    echo "  dev         - Executa em modo de desenvolvimento"
    echo "  build       - Compila para produção"
    echo "  test        - Executa testes"
    echo "  lint        - Executa análise de código"
    echo "  serve       - Serve a aplicação compilada"
    echo "  clean       - Limpa dependências e builds"
    echo "  help        - Mostra esta ajuda"
    echo ""
}

# Função para verificar se o Node.js está instalado
check_node() {
    if ! command -v node &> /dev/null; then
        echo "[ERRO] Node.js não encontrado. Por favor, instale o Node.js 18 ou superior."
        exit 1
    fi
    
    NODE_VERSION=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
    if [ "$NODE_VERSION" -lt 18 ]; then
        echo "[ERRO] Node.js 18 ou superior é requerido. Versão atual: $(node -v)"
        exit 1
    fi
    
    echo "[INFO] Node.js encontrado: $(node -v)"
}

# Função para verificar se o npm está instalado
check_npm() {
    if ! command -v npm &> /dev/null; then
        echo "[ERRO] npm não encontrado. Por favor, instale o npm."
        exit 1
    fi
    echo "[INFO] npm encontrado: $(npm -v)"
}

# Função de setup
setup() {
    echo "[INFO] Verificando pré-requisitos..."
    check_node
    check_npm
    
    echo "[INFO] Instalando dependências..."
    npm install
    
    if [ $? -eq 0 ]; then
        echo "[SUCESSO] Setup concluído com sucesso!"
        echo ""
        echo "Próximos passos:"
        echo "  ./run-frontend.sh dev    - Para executar em modo desenvolvimento"
        echo "  ./run-frontend.sh build  - Para compilar para produção"
    else
        echo "[ERRO] Falha na instalação das dependências."
        exit 1
    fi
}

# Função para modo desenvolvimento
dev() {
    echo "[INFO] Iniciando servidor de desenvolvimento..."
    echo "[INFO] URL da aplicação: http://localhost:4200"
    echo "[INFO] Certifique-se de que o backend esteja rodando na porta 8080"
    echo ""
    
    npm start
}

# Função para build de produção
build() {
    echo "[INFO] Compilando para produção..."
    npm run build:prod
    
    if [ $? -eq 0 ]; then
        echo "[SUCESSO] Build concluído! Arquivos estão na pasta dist/"
    else
        echo "[ERRO] Falha no build."
        exit 1
    fi
}

# Função para executar testes
test() {
    echo "[INFO] Executando testes..."
    npm test
}

# Função para lint
lint() {
    echo "[INFO] Executando análise de código..."
    npm run lint
}

# Função para servir build
serve() {
    echo "[INFO] Servindo aplicação compilada..."
    echo "[INFO] Certifique-se de ter executado o build primeiro"
    
    if [ ! -d "dist" ]; then
        echo "[ERRO] Pasta dist não encontrada. Execute o build primeiro."
        exit 1
    fi
    
    npx http-server dist/projeto-management-frontend -p 4200
}

# Função para limpeza
clean() {
    echo "[INFO] Limpando projeto..."
    
    if [ -d "node_modules" ]; then
        echo "[INFO] Removendo node_modules..."
        rm -rf node_modules
    fi
    
    if [ -d "dist" ]; then
        echo "[INFO] Removendo dist..."
        rm -rf dist
    fi
    
    if [ -f "package-lock.json" ]; then
        echo "[INFO] Removendo package-lock.json..."
        rm package-lock.json
    fi
    
    echo "[SUCESSO] Projeto limpo! Execute './run-frontend.sh setup' para reinstalar."
}

# Processamento dos argumentos
case "$1" in
    setup)
        setup
        ;;
    dev)
        check_node
        check_npm
        dev
        ;;
    build)
        check_node
        check_npm
        build
        ;;
    test)
        check_npm
        test
        ;;
    lint)
        check_npm
        lint
        ;;
    serve)
        serve
        ;;
    clean)
        clean
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
