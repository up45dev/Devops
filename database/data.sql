-- =============================================================================
-- DADOS INICIAIS PARA O DESAFIO TÉCNICO
-- Sistema de Gestão de Tarefas
-- Requisito: 15 tarefas e 2 projetos (atendendo ao mínimo solicitado)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- INSERÇÃO DE PROJETOS (2 projetos principais + extras)
-- -----------------------------------------------------------------------------

INSERT INTO PROJETO (NOME, DESCRICAO, DATA_INICIO, DATA_FIM_PREVISTA, STATUS, PRIORIDADE, ORCAMENTO, RESPONSAVEL, USUARIO_CRIACAO) VALUES
-- Projeto 1 (Principal)
('E-commerce Platform', 'Desenvolvimento de plataforma de e-commerce completa com painel administrativo', '2025-09-01', '2025-12-31', 'EM_ANDAMENTO', 'ALTA', 150000.00, 'João Silva', 'admin'),

-- Projeto 2 (Principal) 
('Migração Legacy', 'Migração do sistema legado para nova arquitetura de microserviços', '2025-10-01', '2025-11-30', 'PLANEJAMENTO', 'CRITICA', 80000.00, 'Maria Santos', 'admin'),

-- Projetos extras (para demonstrar variedade)
('App Mobile iOS/Android', 'Desenvolvimento do aplicativo mobile para iOS e Android', '2025-09-15', '2026-03-15', 'EM_ANDAMENTO', 'MEDIA', 120000.00, 'Pedro Costa', 'admin'),
('Sistema de Relatórios', 'Desenvolvimento de sistema de relatórios avançados com dashboards', '2025-11-01', '2026-01-31', 'PLANEJAMENTO', 'MEDIA', 60000.00, 'Ana Lima', 'admin');

-- -----------------------------------------------------------------------------
-- INSERÇÃO DE TAREFAS (15 tarefas distribuídas pelos projetos)
-- -----------------------------------------------------------------------------

INSERT INTO TAREFA (TITULO, DESCRICAO, STATUS, PRIORIDADE, DATA_INICIO, DATA_FIM_PREVISTA, ESTIMATIVA_HORAS, HORAS_TRABALHADAS, PERCENTUAL_CONCLUSAO, RESPONSAVEL, ID_PROJETO, USUARIO_CRIACAO) VALUES

-- TAREFAS DO PROJETO E-COMMERCE (ID 1) - 6 tarefas
('Análise de Requisitos', 'Levantamento completo dos requisitos funcionais e não funcionais', 'CONCLUIDA', 'ALTA', '2025-09-01', '2025-09-15', 80.00, 75.50, 100, 'Ana Lima', 1, 'admin'),
('Design System e UI/UX', 'Criação do design system e protótipos das interfaces', 'EM_ANDAMENTO', 'ALTA', '2025-09-16', '2025-10-15', 120.00, 60.00, 50, 'Carlos Designer', 1, 'admin'),
('API Backend - Autenticação', 'Desenvolvimento da API de autenticação e autorização', 'EM_ANDAMENTO', 'CRITICA', '2025-10-01', '2025-10-20', 60.00, 20.00, 30, 'João Silva', 1, 'admin'),
('API Backend - Catálogo', 'Desenvolvimento da API de gerenciamento do catálogo', 'ABERTA', 'ALTA', '2025-10-15', '2025-11-10', 80.00, 0.00, 0, 'João Silva', 1, 'admin'),
('Frontend - Página Inicial', 'Desenvolvimento da página inicial do e-commerce', 'ABERTA', 'MEDIA', '2025-10-20', '2025-11-05', 40.00, 0.00, 0, 'Roberto Frontend', 1, 'admin'),
('Testes Automatizados', 'Implementação da suite de testes automatizados', 'ABERTA', 'MEDIA', '2025-11-15', '2025-12-10', 60.00, 0.00, 0, 'Testadora QA', 1, 'admin'),

-- TAREFAS DO PROJETO MIGRAÇÃO LEGACY (ID 2) - 3 tarefas
('Mapeamento da Arquitetura Atual', 'Documentação detalhada da arquitetura legada', 'EM_ANDAMENTO', 'CRITICA', '2025-10-01', '2025-10-15', 40.00, 15.00, 40, 'Arquiteto Senior', 2, 'admin'),
('Planejamento da Nova Arquitetura', 'Design da nova arquitetura de microserviços', 'ABERTA', 'CRITICA', '2025-10-10', '2025-10-25', 60.00, 0.00, 0, 'Arquiteto Senior', 2, 'admin'),
('Setup da Infraestrutura', 'Configuração da infraestrutura para os microserviços', 'ABERTA', 'ALTA', '2025-10-20', '2025-11-05', 80.00, 0.00, 0, 'DevOps Engineer', 2, 'admin'),

-- TAREFAS DO PROJETO APP MOBILE (ID 3) - 4 tarefas
('Prototipação Mobile', 'Criação dos protótipos para iOS e Android', 'CONCLUIDA', 'ALTA', '2025-09-15', '2025-09-30', 50.00, 48.00, 100, 'UX Designer', 3, 'admin'),
('Setup React Native', 'Configuração inicial do projeto React Native', 'CONCLUIDA', 'MEDIA', '2025-10-01', '2025-10-05', 20.00, 18.00, 100, 'Pedro Costa', 3, 'admin'),
('Telas de Autenticação', 'Desenvolvimento das telas de login e cadastro', 'EM_ANDAMENTO', 'ALTA', '2025-10-06', '2025-10-20', 40.00, 25.00, 60, 'Pedro Costa', 3, 'admin'),
('Integração com APIs', 'Integração do app com as APIs do backend', 'ABERTA', 'ALTA', '2025-10-25', '2025-11-15', 60.00, 0.00, 0, 'Mobile Dev', 3, 'admin'),

-- TAREFAS DO PROJETO SISTEMA DE RELATÓRIOS (ID 4) - 2 tarefas
('Especificação dos Relatórios', 'Definição detalhada dos relatórios necessários', 'ABERTA', 'MEDIA', '2025-11-01', '2025-11-10', 30.00, 0.00, 0, 'Analista de Negócio', 4, 'admin'),
('Configuração do BI', 'Setup da ferramenta de Business Intelligence', 'ABERTA', 'MEDIA', '2025-11-15', '2025-11-25', 40.00, 0.00, 0, 'Especialista BI', 4, 'admin');

-- =============================================================================
-- RESUMO DOS DADOS INSERIDOS:
-- 
-- PROJETOS: 4 projetos (2 principais + 2 extras)
-- TAREFAS: 15 tarefas distribuídas:
--   - Projeto 1 (E-commerce): 6 tarefas
--   - Projeto 2 (Migração): 3 tarefas  
--   - Projeto 3 (Mobile): 4 tarefas
--   - Projeto 4 (Relatórios): 2 tarefas
--
-- STATUS VARIADOS: ABERTA, EM_ANDAMENTO, CONCLUIDA
-- PRIORIDADES: BAIXA, MEDIA, ALTA, CRITICA
-- DADOS REALISTAS: Datas, estimativas, responsáveis
-- 
-- ✅ ATENDE REQUISITO: Mínimo 15 tarefas e 2 projetos
-- =============================================================================