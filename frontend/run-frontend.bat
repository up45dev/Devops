@echo off
REM Script para execução do Frontend Angular (Windows)
REM Autor: MiniMax Agent

echo ========================================
echo Frontend Angular - Gerenciamento de Projetos
echo ========================================
echo.

if "%1"=="" (
    echo [ERRO] Comando não especificado.
    echo.
    goto :show_help
)

if "%1"=="setup" goto :setup
if "%1"=="dev" goto :dev
if "%1"=="build" goto :build
if "%1"=="test" goto :test
if "%1"=="lint" goto :lint
if "%1"=="serve" goto :serve
if "%1"=="clean" goto :clean
if "%1"=="help" goto :show_help
if "%1"=="--help" goto :show_help
if "%1"=="-h" goto :show_help

echo [ERRO] Comando desconhecido: %1
echo.
goto :show_help

:show_help
echo Uso: run-frontend.bat [COMANDO]
echo.
echo Comandos disponíveis:
echo   setup       - Instala dependências do projeto
echo   dev         - Executa em modo de desenvolvimento
echo   build       - Compila para produção
echo   test        - Executa testes
echo   lint        - Executa análise de código
echo   serve       - Serve a aplicação compilada
echo   clean       - Limpa dependências e builds
echo   help        - Mostra esta ajuda
echo.
goto :end

:check_node
node --version >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Node.js não encontrado. Por favor, instale o Node.js 18 ou superior.
    exit /b 1
)
echo [INFO] Node.js encontrado
goto :eof

:check_npm
npm --version >nul 2>&1
if errorlevel 1 (
    echo [ERRO] npm não encontrado. Por favor, instale o npm.
    exit /b 1
)
echo [INFO] npm encontrado
goto :eof

:setup
echo [INFO] Verificando pré-requisitos...
call :check_node
if errorlevel 1 goto :end
call :check_npm
if errorlevel 1 goto :end

echo [INFO] Instalando dependências...
npm install
if errorlevel 1 (
    echo [ERRO] Falha na instalação das dependências.
    goto :end
)

echo [SUCESSO] Setup concluído com sucesso!
echo.
echo Próximos passos:
echo   run-frontend.bat dev    - Para executar em modo desenvolvimento
echo   run-frontend.bat build  - Para compilar para produção
goto :end

:dev
echo [INFO] Iniciando servidor de desenvolvimento...
echo [INFO] URL da aplicação: http://localhost:4200
echo [INFO] Certifique-se de que o backend esteja rodando na porta 8080
echo.

call :check_node
if errorlevel 1 goto :end
call :check_npm
if errorlevel 1 goto :end

npm start
goto :end

:build
echo [INFO] Compilando para produção...
call :check_node
if errorlevel 1 goto :end
call :check_npm
if errorlevel 1 goto :end

npm run build:prod
if errorlevel 1 (
    echo [ERRO] Falha no build.
    goto :end
)

echo [SUCESSO] Build concluído! Arquivos estão na pasta dist/
goto :end

:test
echo [INFO] Executando testes...
call :check_npm
if errorlevel 1 goto :end

npm test
goto :end

:lint
echo [INFO] Executando análise de código...
call :check_npm
if errorlevel 1 goto :end

npm run lint
goto :end

:serve
echo [INFO] Servindo aplicação compilada...
echo [INFO] Certifique-se de ter executado o build primeiro

if not exist "dist" (
    echo [ERRO] Pasta dist não encontrada. Execute o build primeiro.
    goto :end
)

npx http-server dist/projeto-management-frontend -p 4200
goto :end

:clean
echo [INFO] Limpando projeto...

if exist "node_modules" (
    echo [INFO] Removendo node_modules...
    rmdir /s /q node_modules
)

if exist "dist" (
    echo [INFO] Removendo dist...
    rmdir /s /q dist
)

if exist "package-lock.json" (
    echo [INFO] Removendo package-lock.json...
    del package-lock.json
)

echo [SUCESSO] Projeto limpo! Execute 'run-frontend.bat setup' para reinstalar.
goto :end

:end
pause
