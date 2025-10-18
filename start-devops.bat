@echo off
REM Script rápido para inicializar a stack DevOps completa
REM Gestão de Tarefas - DevOps Quick Start (Windows)

echo 🚀 Iniciando Stack DevOps - Gestão de Tarefas
echo ================================================

echo 📁 Navegando para diretório DevOps...
cd devops

echo 🔍 Verificando dependências...
docker --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Docker não encontrado. Instale o Docker primeiro.
    pause
    exit /b 1
)

docker-compose --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Docker Compose não encontrado. Instale o Docker Compose primeiro.
    pause
    exit /b 1
)

echo ✅ Dependências verificadas!
echo.
echo 🚀 Executando script principal...
echo.

REM Executar script principal
scripts\start-devops-stack.bat
