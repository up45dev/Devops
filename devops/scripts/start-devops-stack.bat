@echo off
REM Script para inicializar a stack DevOps completa
REM Gestão de Tarefas - DevOps Stack (Windows)

echo 🚀 Iniciando Stack DevOps - Gestão de Tarefas
echo ================================================

REM Verificar se Docker está instalado
docker --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Docker não está instalado. Por favor, instale o Docker primeiro.
    pause
    exit /b 1
)

REM Verificar se Docker Compose está instalado
docker-compose --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Docker Compose não está instalado. Por favor, instale o Docker Compose primeiro.
    pause
    exit /b 1
)

REM Navegar para o diretório DevOps
cd /d "%~dp0"

REM Parar containers existentes
echo [INFO] Parando containers existentes...
docker-compose down --volumes --remove-orphans 2>nul

REM Limpar imagens antigas
echo [INFO] Limpando imagens antigas...
docker system prune -f

REM Construir e iniciar a stack
echo [INFO] Construindo e iniciando a stack DevOps...
docker-compose up --build -d

REM Aguardar os serviços ficarem saudáveis
echo [INFO] Aguardando serviços ficarem saudáveis...
timeout /t 30 /nobreak >nul

REM Verificar status dos containers
echo [INFO] Verificando status dos containers...
docker-compose ps

REM Aguardar inicialização do backend
echo [INFO] Aguardando inicialização do backend (pode levar até 2 minutos)...
timeout /t 60 /nobreak >nul

REM Mostrar URLs de acesso
echo.
echo [SUCCESS] Stack DevOps iniciada com sucesso!
echo ================================================
echo 🌐 URLs de Acesso:
echo   • Frontend:     http://localhost
echo   • Backend API:  http://localhost:8080
echo   • Prometheus:   http://localhost:9090
echo   • Grafana:      http://localhost:3000 (admin/admin)
echo   • PostgreSQL:   localhost:5432 (admin/admin123)
echo.
echo 📊 Monitoramento:
echo   • Métricas da aplicação disponíveis no Prometheus
echo   • Dashboards personalizados no Grafana
echo   • Health checks automáticos configurados
echo.
echo 💡 Comandos úteis:
echo   • Ver logs:           docker-compose logs -f [serviço]
echo   • Parar stack:       docker-compose down
echo   • Restart serviço:   docker-compose restart [serviço]
echo   • Status containers: docker-compose ps
echo.
echo ✅ Pronto para uso!
echo.
pause
