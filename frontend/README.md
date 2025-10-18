# Frontend Angular - Sistema de Gerenciamento de Projetos

Interface frontend moderna desenvolvida com Angular 17 para consumir a API REST do sistema de gerenciamento de projetos e tarefas.

## 🎯 Funcionalidades Implementadas

### ✅ **Formulário de Tarefas**
- Cadastro completo de tarefas com todos os campos
- Seleção de projeto obrigatória
- Validações robustas de entrada
- Edição de tarefas existentes
- Interface responsiva e intuitiva

### ✅ **Grid de Listagem**
- Tabela completa com todas as informações das tarefas
- **Paginação** configurável (5, 10, 25, 50 itens)
- **Filtro por projeto** e outros critérios
- **Botão de exclusão** com confirmação
- Ordenação e busca avançada

### ✨ **Recursos Avançados**
- **Design moderno** com Angular Material
- **Interface responsiva** para mobile e desktop
- **Notificações** de sucesso e erro
- **Loading states** durante operações
- **Tratamento de erros** globalizado
- **Validações em tempo real**
- **Componentes reutilizáveis**

## 📱 Interface do Usuário

### **Tela Principal**
- **Toolbar superior** com seleção de projeto
- **Sistema de abas** para navegação intuitiva
- **Aba 1**: Formulário de nova tarefa/edição
- **Aba 2**: Lista de tarefas com filtros

### **Formulário de Tarefa**
- Título (obrigatório)
- Projeto (seleção obrigatória)
- Status e Prioridade
- Datas de início e fim
- Estimativa e horas trabalhadas
- Percentual de conclusão
- Responsável, descrição e observações

### **Grid de Tarefas**
- **Colunas**: Título, Projeto, Status, Prioridade, Responsável, Prazo, Progresso, Ações
- **Filtros**: Por projeto, título, status, prioridade, responsável
- **Ações**: Editar e excluir com confirmação
- **Indicadores visuais**: Tarefas atrasadas, progress bars, chips coloridos

## 🚀 Tecnologias Utilizadas

- **Angular 17** - Framework principal (standalone components)
- **Angular Material** - Componentes de UI
- **TypeScript** - Linguagem de programação
- **RxJS** - Programação reativa
- **SCSS** - Estilização avançada
- **Responsive Design** - Layout adaptável

## 💾 Instalação e Execução

### **Pré-requisitos**
- Node.js 18 ou superior
- npm 9 ou superior
- Backend rodando na porta 8080

### **1. Instalação**
```bash
# Linux/Mac
cd frontend
./run-frontend.sh setup

# Windows
cd frontend
run-frontend.bat setup
```

### **2. Desenvolvimento**
```bash
# Linux/Mac
./run-frontend.sh dev

# Windows
run-frontend.bat dev
```

### **3. Produção**
```bash
# Build
./run-frontend.sh build

# Servir
./run-frontend.sh serve
```

## 🌐 URLs da Aplicação

- **Frontend**: `http://localhost:4200`
- **Backend API**: `http://localhost:8080/api`
- **Swagger**: `http://localhost:8080/swagger-ui.html`

## 📁 Estrutura do Projeto

```
frontend/
├── src/
│   ├── app/
│   │   ├── components/
│   │   │   ├── tarefa-form/          # Formulário de tarefas
│   │   │   ├── tarefa-list/          # Grid de listagem
│   │   │   └── confirm-dialog/       # Diálogo de confirmação
│   │   ├── models/               # Interfaces TypeScript
│   │   ├── services/             # Services para API
│   │   ├── shared/               # Interceptors e utilitários
│   │   ├── app.component.*       # Componente principal
│   │   └── app.config.ts         # Configuração da app
│   ├── environments/         # Configurações de ambiente
│   ├── main.ts              # Bootstrap da aplicação
│   └── styles.scss          # Estilos globais
├── package.json         # Dependências
├── angular.json         # Configuração do Angular
└── tsconfig.json        # Configuração TypeScript
```

## 🛠️ Arquitetura

### **Componentes Standalone**
- Utiliza a nova arquitetura standalone do Angular 17
- Componentes independentes sem módulos
- Imports diretos e otimizados

### **Services**
- `ApiService` - Service base para HTTP
- `ProjetoService` - Gerenciamento de projetos
- `TarefaService` - Gerenciamento de tarefas
- `NotificationService` - Notificações

### **Interceptors**
- `HttpInterceptorService` - Tratamento global de erros
- Headers automáticos
- Notificações de erro centralizadas

## 📱 Responsividade

### **Desktop (> 768px)**
- Layout completo com todas as colunas
- Filtros em linha horizontal
- Formulário em duas colunas

### **Tablet (768px - 600px)**
- Colunas adaptadas
- Filtros empilhados
- Formulário em coluna única

### **Mobile (< 600px)**
- Tabela simplificada
- Filtros colapsados
- Botões full-width

## 🎨 Design System

### **Cores Principais**
- **Primária**: Azul (#1976D2)
- **Secundária**: Azul claro (#2196F3)
- **Sucesso**: Verde (#4CAF50)
- **Erro**: Vermelho (#F44336)
- **Aviso**: Laranja (#FF9800)

### **Status e Prioridades**
- **Status**: Chips coloridos por estado
- **Prioridades**: Escala de cores de baixa a crítica
- **Progress bars**: Indicadores visuais de progresso

## 🧪 Testes

```bash
# Executar testes unitários
./run-frontend.sh test

# Análise de código
./run-frontend.sh lint
```

## 🔧 Scripts Disponíveis

| Comando | Descrição |
|---------|-------------|
| `setup` | Instala dependências |
| `dev` | Modo desenvolvimento |
| `build` | Build de produção |
| `test` | Executa testes |
| `lint` | Análise de código |
| `serve` | Serve build |
| `clean` | Limpa projeto |

## 📝 API Integration

### **Endpoints Consumidos**
- `GET /api/projetos` - Lista projetos
- `GET /api/tarefas` - Lista tarefas (com filtros e paginação)
- `POST /api/tarefas` - Cria tarefa
- `PUT /api/tarefas/{id}` - Atualiza tarefa
- `DELETE /api/tarefas/{id}` - Exclui tarefa
- `PATCH /api/tarefas/{id}/status` - Altera status

### **Tratamento de Erros**
- Interceptor global para tratamento HTTP
- Notificações automáticas de erro
- Fallbacks para estados de carregamento

## 🚀 Funcionalidades Futuras

- [ ] Drag & drop para reordenar tarefas
- [ ] Filtros avançados com data ranges
- [ ] Dashboard com gráficos
- [ ] Exportação de dados
- [ ] Modo escuro
- [ ] PWA (Progressive Web App)
- [ ] Notificações push
- [ ] Chat colaborativo

---

**Desenvolvido por MiniMax Agent** | **Angular 17** | **2025-09-28**
