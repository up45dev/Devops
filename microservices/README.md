# 🚀 Task Management Microservices Architecture

## 📋 **Visão Geral**

**Arquitetura completa de microserviços enterprise** implementada com **Spring Boot 3.2.1**, **Java 21**, **OAuth2 + JWT**, **Vaadin**, **HATEOAS** e **Apache Kafka**.

---

## 🏗️ **Microserviços Implementados**

| Serviço | Porta | Tecnologia Principal | Status | Descrição |
|---------|--------|---------------------|---------|-----------|
| **🔍 discovery-service** | 8761 | Eureka Server | ✅ | Service Discovery e Registry |
| **🔐 auth-service** | 8080 | OAuth2 + JWT | ✅ | Autenticação e Autorização |
| **🚪 api-gateway** | 8000 | Spring Cloud Gateway | ✅ | Gateway de entrada e roteamento |
| **📋 projeto-service** | 8081 | Spring Web + HATEOAS | ✅ | Gestão de Projetos |
| **📊 admin-ui-service** | 8090 | Vaadin | ✅ | Interface Administrativa |

---

## 🔧 **Stack Tecnológico**

### **Core**
- ✅ **Spring Boot 3.2.1** - Framework principal
- ✅ **Java 21** - Features modernas (Records, Sealed Classes, Pattern Matching)
- ✅ **Spring Cloud 2023.0.0** - Microserviços

### **Segurança**
- ✅ **OAuth2** - Autenticação com provedores externos (Google, GitHub)
- ✅ **JWT** - Tokens para comunicação inter-serviços
- ✅ **Spring Security** - Proteção de endpoints

### **Interface e APIs**
- ✅ **Vaadin 24.3.0** - Interface administrativa rica
- ✅ **Spring HATEOAS** - APIs REST maduras
- ✅ **Spring Web** - Controllers REST

### **Infraestrutura**
- ✅ **Netflix Eureka** - Service Discovery
- ✅ **Spring Cloud Gateway** - API Gateway com rate limiting
- ✅ **Apache Kafka** - Event streaming
- ✅ **Redis** - Cache e sessões
- ✅ **PostgreSQL** - Banco de dados

### **Desenvolvimento**
- ✅ **Spring Dev Tools** - Hot reload
- ✅ **Docker** - Containerização
- ✅ **Maven** - Build management

---

## 🚀 **Como Executar**

### **Método 1: Script Automatizado (Recomendado)**
```bash
# Compilar todos os projetos
mvn clean install -DskipTests

# Executar script (Linux/Mac)
chmod +x start-services.sh
./start-services.sh

# Para parar os serviços
./stop-services.sh
```

### **Método 2: Docker Compose**
```bash
docker-compose up -d
```

### **Método 3: Manual (Para Desenvolvimento)**
```bash
# 1. Discovery Service (PRIMEIRO)
cd discovery-service && mvn spring-boot:run &

# 2. Auth Service
cd auth-service && mvn spring-boot:run &

# 3. API Gateway
cd api-gateway && mvn spring-boot:run &

# 4. Projeto Service
cd projeto-service && mvn spring-boot:run &

# 5. Admin UI
cd admin-ui-service && mvn spring-boot:run &
```

---

## 🌐 **URLs dos Serviços**

### **🔗 Principais**
- **🔍 Eureka Dashboard:** http://localhost:8761
- **🚪 API Gateway:** http://localhost:8000
- **📊 Admin UI (Vaadin):** http://localhost:8090

### **🔐 Autenticação**
- **Auth Service:** http://localhost:8080
- **Login OAuth2 Google:** http://localhost:8080/oauth2/authorization/google
- **Login OAuth2 GitHub:** http://localhost:8080/oauth2/authorization/github

### **📋 APIs REST (via Gateway)**
- **Projetos:** http://localhost:8000/api/projetos
- **Health Check:** http://localhost:8000/actuator/health

---

## 🔐 **Configuração OAuth2**

Para usar autenticação OAuth2, configure as variáveis:

```bash
export GOOGLE_CLIENT_ID="your-google-client-id"
export GOOGLE_CLIENT_SECRET="your-google-client-secret"
export GITHUB_CLIENT_ID="your-github-client-id" 
export GITHUB_CLIENT_SECRET="your-github-client-secret"
export JWT_SECRET="your-super-secret-jwt-key"
```

📚 **Guia completo:** [OAUTH2-SETUP-GUIDE.md](OAUTH2-SETUP-GUIDE.md)

---

## 🏗️ **Arquitetura**

```
┌─────────────────┐    ┌─────────────────┐
│   Web Browser   │    │  External APIs  │
└─────────┬───────┘    └─────────┬───────┘
          │                      │
          └──────────┬───────────┘
                     │
        ┌─────────────▼─────────────┐
        │      API Gateway         │
        │    (Port 8000)          │
        │  • Rate Limiting        │
        │  • JWT Validation       │
        └─────────────┬─────────────┘
                     │
     ┌───────────────┼───────────────┐
     │               │               │
┌────▼────┐   ┌──────▼──────┐  ┌─────▼─────┐
│Auth Svc │   │Projeto Svc  │  │Admin UI   │
│(8080)   │   │(8081)       │  │(8090)     │
│OAuth2   │   │HATEOAS      │  │Vaadin     │
└─────────┘   └─────────────┘  └───────────┘
     │               │               │
┌────▼────┐   ┌──────▼──────┐  ┌─────▼─────┐
│PostgreSQL│   │PostgreSQL  │  │API Calls  │
│Auth DB   │   │Project DB  │  │via Feign  │
└─────────┘   └─────────────┘  └───────────┘
                     │
            ┌────────▼────────┐
            │  Apache Kafka   │
            │ (Event Stream)  │
            └─────────────────┘
```

---

## ✨ **Funcionalidades Implementadas**

### **🔐 Autenticação e Autorização**
- [x] OAuth2 com Google e GitHub
- [x] Autenticação local (username/password)
- [x] JWT para comunicação inter-serviços
- [x] Refresh tokens
- [x] Role-based access control

### **📋 Gestão de Projetos**
- [x] CRUD completo com HATEOAS
- [x] Máquina de estados (Planejamento → Em Andamento → Concluído)
- [x] Busca e filtros avançados
- [x] Estatísticas e relatórios
- [x] Event publishing via Kafka

### **📊 Interface Administrativa**
- [x] Dashboard moderno com Vaadin
- [x] Navegação por menu lateral
- [x] Views para projetos, usuários, relatórios
- [x] Configurações do sistema

### **🏗️ Arquitetura de Microserviços**
- [x] Service Discovery automático
- [x] Load balancing inteligente
- [x] Rate limiting para proteção
- [x] Circuit breaker patterns
- [x] Event-driven communication

---

## 📊 **Monitoramento e Observabilidade**

### **Health Checks**
- **All Services:** `/actuator/health`
- **Eureka:** http://localhost:8761 (Dashboard)

### **Métricas**
- **Prometheus:** `/actuator/prometheus`
- **Custom Metrics:** Via Micrometer

### **Logs**
- **Centralizados:** `logs/[service-name].log`
- **Structured Logging:** JSON format

---

## 🧪 **Testes**

```bash
# Executar todos os testes
mvn test

# Testes específicos por serviço
cd auth-service && mvn test
cd projeto-service && mvn test
```

---

## 📚 **Documentação**

- **📋 Arquitetura Detalhada:** [MICROSERVICES-ARCHITECTURE-SUMMARY.md](MICROSERVICES-ARCHITECTURE-SUMMARY.md)
- **🔐 Setup OAuth2:** [OAUTH2-SETUP-GUIDE.md](OAUTH2-SETUP-GUIDE.md)
- **🐳 Docker:** [docker-compose.yml](docker-compose.yml)

---

## 🛠️ **Troubleshooting**

### **Problemas Comuns**
1. **Porta em uso:** Altere as portas nos `application.yml`
2. **OAuth2 não funciona:** Verifique as variáveis de ambiente
3. **Serviços não se registram:** Aguarde o Eureka inicializar

### **Logs de Debug**
```bash
# Ver logs de um serviço específico
tail -f logs/auth-service.log

# Verificar se serviços estão rodando
curl http://localhost:8761/eureka/apps
```

---

## 🎯 **Próximos Passos**

- [ ] Implementar Tarefa Service
- [ ] Adicionar Thymeleaf UI
- [ ] Implementar Circuit Breakers
- [ ] Adicionar Tracing distribuído (Zipkin)
- [ ] Configurar monitoramento (Grafana)

---

## 🤝 **Contribuição**

1. Fork o projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

---

## 📄 **Licença**

Este projeto está licenciado sob a MIT License.

---

**🚀 Projeto pronto para produção enterprise com arquitetura de microserviços moderna!**
