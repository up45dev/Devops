@echo off
REM Script para execução do Sistema de Gerenciamento de Projetos e Tarefas (Windows)
REM Autor: MiniMax Agent

echo ========================================
echo Sistema de Gerenciamento de Projetos
echo ========================================
echo.

if "%1"=="" (
    echo [ERRO] Comando não especificado.
    echo.
    goto :show_help
)

if "%1"=="setup" goto :setup
if "%1"=="dev" goto :dev
if "%1"=="prod" goto :prod
if "%1"=="test" goto :test
if "%1"=="clean" goto :clean
if "%1"=="docs" goto :docs
if "%1"=="db-setup" goto :db_setup
if "%1"=="help" goto :show_help
if "%1"=="--help" goto :show_help
if "%1"=="-h" goto :show_help

echo [ERRO] Comando desconhecido: %1
echo.
goto :show_help

:show_help
echo Uso: run.bat [COMANDO]
echo.
echo Comandos disponíveis:
echo   setup       - Configuração inicial do projeto
echo   dev         - Executa em modo de desenvolvimento (H2)
echo   prod        - Compila e executa em modo de produção (MySQL)
echo   test        - Executa todos os testes
echo   clean       - Limpa o projeto
echo   docs        - Abre a documentação da API
echo   db-setup    - Configura o banco MySQL
echo   help        - Mostra esta ajuda
echo.
goto :end

:check_maven
mvn --version >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Maven não encontrado. Por favor, instale o Apache Maven.
    exit /b 1
)
echo [INFO] Maven encontrado
goto :eof

:check_java
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Java não encontrado. Por favor, instale o Java 17 ou superior.
    exit /b 1
)
echo [INFO] Java encontrado
goto :eof

:setup
echo [INFO] Verificando pré-requisitos...
call :check_java
if errorlevel 1 goto :end
call :check_maven
if errorlevel 1 goto :end

echo [INFO] Compilando o projeto...
mvn clean compile
if errorlevel 1 (
    echo [ERRO] Falha na compilação. Verifique os logs acima.
    goto :end
)

echo [SUCESSO] Setup concluído com sucesso!
echo.
echo Próximos passos:
echo   run.bat dev      - Para executar em modo desenvolvimento
echo   run.bat test     - Para executar os testes
goto :end

:dev
echo [INFO] Iniciando em modo de desenvolvimento...
echo [INFO] Banco de dados: H2 (em memória)
echo [INFO] URL da API: http://localhost:8080/api
echo [INFO] Swagger: http://localhost:8080/swagger-ui.html
echo [INFO] H2 Console: http://localhost:8080/h2-console
echo.

call :check_java
if errorlevel 1 goto :end
call :check_maven
if errorlevel 1 goto :end

mvn spring-boot:run -Dspring-boot.run.profiles=dev
goto :end

:prod
echo [INFO] Compilando para produção...
call :check_java
if errorlevel 1 goto :end
call :check_maven
if errorlevel 1 goto :end

mvn clean package -DskipTests
if errorlevel 1 (
    echo [ERRO] Falha na compilação.
    goto :end
)

echo [INFO] Iniciando em modo de produção...
echo [INFO] Banco de dados: MySQL
echo [INFO] URL da API: http://localhost:8080/api
echo.

java -jar target/projeto-management-1.0.0.jar
goto :end

:test
echo [INFO] Executando testes...
call :check_maven
if errorlevel 1 goto :end

mvn clean test
if errorlevel 1 (
    echo [ERRO] Alguns testes falharam.
    goto :end
)

echo [SUCESSO] Todos os testes passaram!
goto :end

:clean
echo [INFO] Limpando o projeto...
call :check_maven
if errorlevel 1 goto :end

mvn clean
echo [SUCESSO] Projeto limpo!
goto :end

:docs
echo [INFO] Abrindo documentação da API...
echo [INFO] Certifique-se de que a aplicação está rodando primeiro.
echo.
echo URLs da documentação:
echo   Swagger UI: http://localhost:8080/swagger-ui.html
echo   OpenAPI JSON: http://localhost:8080/api-docs
echo.

start "" "http://localhost:8080/swagger-ui.html"
goto :end

:db_setup
echo [INFO] Configurando banco de dados MySQL...
echo.
echo Execute os seguintes comandos no MySQL:
echo.
echo CREATE DATABASE projeto_management;
echo CREATE USER 'projeto_user'@'localhost' IDENTIFIED BY 'projeto_pass';
echo GRANT ALL PRIVILEGES ON projeto_management.* TO 'projeto_user'@'localhost';
echo FLUSH PRIVILEGES;
echo.
echo Depois execute:
echo mysql -u projeto_user -p projeto_management ^< database/schema.sql
echo.
echo Ajuste as configurações em src/main/resources/application.properties se necessário.
goto :end

:end
pause
