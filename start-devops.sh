#!/bin/bash

# Script rápido para inicializar a stack DevOps completa
# Gestão de Tarefas - DevOps Quick Start

echo "🚀 Iniciando Stack DevOps - Gestão de Tarefas"
echo "================================================"

echo "📁 Navegando para diretório DevOps..."
cd devops

echo "🔍 Verificando dependências..."
if ! command -v docker &> /dev/null; then
    echo "❌ Docker não encontrado. Instale o Docker primeiro."
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose não encontrado. Instale o Docker Compose primeiro."
    exit 1
fi

echo "✅ Dependências verificadas!"
echo ""
echo "🚀 Executando script principal..."
echo ""

# Tornar script executável (se possível)
chmod +x scripts/start-devops-stack.sh 2>/dev/null || true

# Executar script principal
bash scripts/start-devops-stack.sh
