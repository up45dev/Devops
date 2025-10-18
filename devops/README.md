# 🚀 Stack DevOps - Gestão de Tarefas

## 📋 Visão Geral

Este diretório contém uma stack DevOps completa para o projeto de Gestão de Tarefas, incluindo:

- **🐳 Containerização** com Docker
- **📊 Monitoramento** com Prometheus + Grafana
- **⚙️ CI/CD** com Jenkins
- **☸️ Orquestração** com Kubernetes
- **🔄 Automação** com scripts personalizados
- **💾 Backup** automatizado
- **🔍 Health Monitoring** em tempo real

## 🏗️ Arquitetura

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │   PostgreSQL    │
│   (Angular)     │◄──►│  (Spring Boot)  │◄──►│   Database      │
│   Port: 80      │    │   Port: 8080    │    │   Port: 5432    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Monitoring Stack                          │
│  ┌─────────────────┐    ┌─────────────────┐                  │
│  │   Prometheus    │◄──►│     Grafana     │                  │
│  │   Port: 9090    │    │   Port: 3000    │                  │
│  └─────────────────┘    └─────────────────┘                  │
└─────────────────────────────────────────────────────────────────┘
```

## 🚀 Quick Start

### Pré-requisitos
- Docker 20.10+
- Docker Compose 2.0+
- Git

### Iniciando a Stack

#### Linux/macOS
```bash
cd devops
./scripts/start-devops-stack.sh
```

#### Windows
```cmd
cd devops
scripts\start-devops-stack.bat
```

### URLs de Acesso

| Serviço | URL | Credenciais |
|---------|-----|-------------|
| 🌐 Frontend | http://localhost | - |
| 🔧 Backend API | http://localhost:8080 | - |
| 📊 Grafana | http://localhost:3000 | admin/admin |
| 📈 Prometheus | http://localhost:9090 | - |
| 🗄️ PostgreSQL | localhost:5432 | admin/admin123 |

## 📊 Monitoramento

### Métricas Disponíveis

- **Application Metrics**: Response time, throughput, error rate
- **JVM Metrics**: Memory usage, garbage collection, threads
- **Database Metrics**: Connection pool, query performance
- **System Metrics**: CPU, memory, disk usage

### Dashboards Grafana

1. **Application Dashboard**: Métricas específicas da aplicação
2. **JVM Dashboard**: Monitoramento da JVM
3. **Infrastructure Dashboard**: Métricas do sistema

### Alertas Configurados

- 🚨 **Application Down**: Aplicação indisponível
- ⚠️ **High Response Time**: Tempo de resposta > 1s
- 🔥 **High Error Rate**: Taxa de erro > 5%
- 💾 **High Memory Usage**: Uso de memória > 85%
- 🗃️ **Database Issues**: Problemas de conexão

## 🔄 CI/CD Pipeline

### Estágios do Pipeline

1. **Checkout**: Clone do código
2. **Build Backend**: Compilação do Spring Boot
3. **Test Backend**: Execução de testes + coverage
4. **Build Frontend**: Build do Angular
5. **Code Quality**: SonarQube + OWASP
6. **Quality Gate**: Verificação de qualidade
7. **Docker Build**: Criação das imagens
8. **Security Scan**: Análise de vulnerabilidades
9. **Deploy Staging**: Deploy automático (develop)
10. **Integration Tests**: Testes de integração
11. **Deploy Production**: Deploy manual (main)

### Ferramentas Integradas

- **SonarQube**: Análise de qualidade de código
- **OWASP Dependency Check**: Análise de vulnerabilidades
- **Trivy**: Scanner de segurança para containers
- **JaCoCo**: Coverage de código
- **Slack**: Notificações

## 🐳 Docker

### Imagens Criadas

- `gestao-tarefas-backend`: Spring Boot application
- `gestao-tarefas-frontend`: Angular + Nginx

### Volumes Persistentes

- `postgres_data`: Dados do PostgreSQL
- `prometheus_data`: Dados do Prometheus
- `grafana_data`: Configurações do Grafana

## ☸️ Kubernetes

### Recursos Criados

- **Namespace**: gestao-tarefas
- **Deployments**: Backend (2 replicas), Frontend (2 replicas)
- **Services**: ClusterIP para backend, LoadBalancer para frontend
- **Ingress**: Roteamento com SSL/TLS
- **ConfigMaps**: Configurações da aplicação
- **Secrets**: Credenciais sensíveis

### Deploy no Kubernetes

```bash
kubectl apply -f devops/kubernetes/deployment.yaml
```

## 🔧 Scripts de Automação

### Health Monitor
```bash
./devops/scripts/health-monitor.sh
```
- Verifica saúde de todos os serviços
- Monitora métricas em tempo real
- Envia alertas via Slack
- Gera relatórios JSON

### Backup Automatizado
```bash
./devops/scripts/backup.sh
```
- Backup do banco PostgreSQL
- Backup dos volumes Docker
- Upload para S3 (opcional)
- Retenção configurável
- Notificações Slack

## 📁 Estrutura de Diretórios

```
devops/
├── docker/
│   ├── Dockerfile.backend
│   ├── Dockerfile.frontend
│   └── docker-compose.yml
├── monitoring/
│   ├── prometheus/
│   │   ├── prometheus.yml
│   │   └── alerts.yml
│   └── grafana/
│       ├── provisioning/
│       └── dashboards/
├── jenkins/
│   └── Jenkinsfile
├── kubernetes/
│   └── deployment.yaml
├── nginx/
│   ├── nginx.conf
│   └── default.conf
└── scripts/
    ├── start-devops-stack.sh
    ├── start-devops-stack.bat
    ├── health-monitor.sh
    └── backup.sh
```

## 🛠️ Comandos Úteis

### Docker Compose
```bash
# Iniciar stack
docker-compose up -d

# Ver logs
docker-compose logs -f [serviço]

# Parar stack
docker-compose down

# Rebuild e restart
docker-compose up --build -d

# Status dos containers
docker-compose ps
```

### Monitoramento
```bash
# Health check manual
curl http://localhost:8080/actuator/health

# Métricas Prometheus
curl http://localhost:8080/actuator/prometheus

# Status do Prometheus
curl http://localhost:9090/-/healthy
```

### Backup e Restore
```bash
# Backup manual
./devops/scripts/backup.sh

# Restore do banco
docker exec -i gestao-tarefas-db psql -U admin gestao_tarefas < backup.sql
```

## 🔐 Segurança

### Configurações de Segurança

- **Container Security**: Usuários não-root, recursos limitados
- **Network Security**: Rede isolada entre containers
- **Secrets Management**: Variáveis sensíveis em secrets
- **Security Headers**: Headers de segurança no Nginx
- **Vulnerability Scanning**: Trivy integrado no pipeline

### Hardening

- Base images otimizadas e atualizadas
- Health checks em todos os containers
- Resource limits configurados
- Read-only filesystems onde possível

## 📈 Métricas e KPIs

### SLIs (Service Level Indicators)

- **Availability**: Uptime da aplicação
- **Latency**: P50, P95, P99 response times
- **Error Rate**: 4xx e 5xx responses
- **Throughput**: Requests per second

### SLOs (Service Level Objectives)

- Availability: 99.9%
- P95 Latency: < 500ms
- Error Rate: < 1%
- Recovery Time: < 5 minutes

## 🚨 Troubleshooting

### Problemas Comuns

#### Container não inicia
```bash
# Verificar logs
docker-compose logs [container]

# Verificar recursos
docker system df
docker system prune
```

#### Aplicação lenta
```bash
# Verificar métricas no Grafana
# Analisar logs de performance
docker-compose logs backend | grep -i "slow"
```

#### Falha no health check
```bash
# Executar health monitor
./devops/scripts/health-monitor.sh

# Verificar endpoints manualmente
curl -v http://localhost:8080/actuator/health
```

## 🤝 Contribuindo

1. Fork do projeto
2. Criar branch feature (`git checkout -b feature/nova-feature`)
3. Commit das mudanças (`git commit -am 'Adicionar nova feature'`)
4. Push para branch (`git push origin feature/nova-feature`)
5. Criar Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](../LICENSE) para mais detalhes.

---

**💡 Dica**: Para uma experiência completa, certifique-se de que todos os serviços estejam rodando antes de acessar as URLs. Use o script `health-monitor.sh` para verificar o status.
