-- =============================================================================
-- SISTEMA DE GERENCIAMENTO DE PROJETOS E TAREFAS
-- Modelagem de Dados Melhorada
-- Data: 2025-09-28
-- =============================================================================

-- Criar banco de dados
CREATE DATABASE IF NOT EXISTS projeto_management;
USE projeto_management;

-- Tabela PROJETO (Melhorada)
CREATE TABLE projeto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    data_inicio DATE,
    data_fim_prevista DATE,
    data_fim_real DATE,
    status ENUM('PLANEJAMENTO', 'EM_ANDAMENTO', 'PAUSADO', 'CONCLUIDO', 'CANCELADO') DEFAULT 'PLANEJAMENTO',
    prioridade ENUM('BAIXA', 'MEDIA', 'ALTA', 'CRITICA') DEFAULT 'MEDIA',
    orcamento DECIMAL(15,2),
    responsavel VARCHAR(255),
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    usuario_criacao VARCHAR(100),
    usuario_atualizacao VARCHAR(100),
    ativo BOOLEAN DEFAULT TRUE,
    
    -- Índices para performance
    INDEX idx_projeto_nome (nome),
    INDEX idx_projeto_status (status),
    INDEX idx_projeto_data_criacao (data_criacao),
    INDEX idx_projeto_responsavel (responsavel)
);

-- Tabela TAREFA (Melhorada)
CREATE TABLE tarefa (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    status ENUM('ABERTA', 'EM_ANDAMENTO', 'EM_REVISAO', 'CONCLUIDA', 'CANCELADA') DEFAULT 'ABERTA',
    prioridade ENUM('BAIXA', 'MEDIA', 'ALTA', 'CRITICA') DEFAULT 'MEDIA',
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_inicio DATE,
    data_fim_prevista DATE,
    data_fim_real DATE,
    estimativa_horas DECIMAL(8,2),
    horas_trabalhadas DECIMAL(8,2) DEFAULT 0.00,
    percentual_conclusao TINYINT DEFAULT 0 CHECK (percentual_conclusao >= 0 AND percentual_conclusao <= 100),
    responsavel VARCHAR(255),
    observacoes TEXT,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    usuario_criacao VARCHAR(100),
    usuario_atualizacao VARCHAR(100),
    ativo BOOLEAN DEFAULT TRUE,
    
    -- Chave estrangeira
    id_projeto BIGINT NOT NULL,
    
    -- Constraints
    CONSTRAINT fk_tarefa_projeto FOREIGN KEY (id_projeto) REFERENCES projeto(id) ON DELETE CASCADE,
    CONSTRAINT chk_data_fim CHECK (data_fim_real IS NULL OR data_fim_real >= data_inicio),
    CONSTRAINT chk_horas CHECK (horas_trabalhadas >= 0),
    
    -- Índices para performance
    INDEX idx_tarefa_titulo (titulo),
    INDEX idx_tarefa_status (status),
    INDEX idx_tarefa_projeto (id_projeto),
    INDEX idx_tarefa_responsavel (responsavel),
    INDEX idx_tarefa_data_criacao (data_criacao),
    INDEX idx_tarefa_prioridade (prioridade),
    INDEX idx_tarefa_data_fim_prevista (data_fim_prevista)
);

-- Tabela de auditoria para mudanças de status
CREATE TABLE auditoria_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tabela ENUM('PROJETO', 'TAREFA') NOT NULL,
    id_registro BIGINT NOT NULL,
    status_anterior VARCHAR(50),
    status_novo VARCHAR(50) NOT NULL,
    data_mudanca TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario VARCHAR(100),
    observacao TEXT,
    
    INDEX idx_auditoria_tabela_registro (tabela, id_registro),
    INDEX idx_auditoria_data (data_mudanca)
);

-- Tabela para comentários/histórico
CREATE TABLE comentario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_entidade ENUM('PROJETO', 'TAREFA') NOT NULL,
    id_entidade BIGINT NOT NULL,
    comentario TEXT NOT NULL,
    data_comentario TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario VARCHAR(100),
    
    INDEX idx_comentario_entidade (tipo_entidade, id_entidade),
    INDEX idx_comentario_data (data_comentario)
);

-- Views úteis para relatórios
CREATE VIEW vw_projetos_resumo AS
SELECT 
    p.id,
    p.nome,
    p.status,
    p.prioridade,
    p.data_inicio,
    p.data_fim_prevista,
    p.responsavel,
    COUNT(t.id) as total_tarefas,
    COUNT(CASE WHEN t.status = 'CONCLUIDA' THEN 1 END) as tarefas_concluidas,
    ROUND(COUNT(CASE WHEN t.status = 'CONCLUIDA' THEN 1 END) * 100.0 / COUNT(t.id), 2) as percentual_conclusao,
    SUM(COALESCE(t.estimativa_horas, 0)) as total_horas_estimadas,
    SUM(COALESCE(t.horas_trabalhadas, 0)) as total_horas_trabalhadas
FROM projeto p
LEFT JOIN tarefa t ON p.id = t.id_projeto AND t.ativo = TRUE
WHERE p.ativo = TRUE
GROUP BY p.id, p.nome, p.status, p.prioridade, p.data_inicio, p.data_fim_prevista, p.responsavel;

CREATE VIEW vw_tarefas_atrasadas AS
SELECT 
    t.id,
    t.titulo,
    t.status,
    t.prioridade,
    t.data_fim_prevista,
    t.responsavel,
    p.nome as nome_projeto,
    DATEDIFF(CURDATE(), t.data_fim_prevista) as dias_atraso
FROM tarefa t
INNER JOIN projeto p ON t.id_projeto = p.id
WHERE t.ativo = TRUE 
    AND t.status NOT IN ('CONCLUIDA', 'CANCELADA')
    AND t.data_fim_prevista < CURDATE();

-- Inserir dados de exemplo
INSERT INTO projeto (nome, descricao, data_inicio, data_fim_prevista, status, prioridade, responsavel, usuario_criacao) VALUES
('Sistema de Vendas Online', 'Desenvolvimento de plataforma e-commerce completa', '2025-09-01', '2025-12-31', 'EM_ANDAMENTO', 'ALTA', 'João Silva', 'admin'),
('Migração de Dados', 'Migração do sistema legado para nova arquitetura', '2025-10-01', '2025-11-30', 'PLANEJAMENTO', 'CRITICA', 'Maria Santos', 'admin'),
('App Mobile', 'Desenvolvimento do aplicativo mobile', '2025-09-15', '2026-03-15', 'EM_ANDAMENTO', 'MEDIA', 'Pedro Costa', 'admin');

INSERT INTO tarefa (titulo, descricao, status, prioridade, data_inicio, data_fim_prevista, estimativa_horas, responsavel, id_projeto, usuario_criacao) VALUES
('Análise de Requisitos', 'Levantamento completo dos requisitos do sistema', 'CONCLUIDA', 'ALTA', '2025-09-01', '2025-09-15', 80.00, 'Ana Lima', 1, 'admin'),
('Design da Interface', 'Criação dos layouts e protótipos', 'EM_ANDAMENTO', 'MEDIA', '2025-09-16', '2025-10-15', 120.00, 'Carlos Design', 1, 'admin'),
('Desenvolvimento Backend', 'Implementação da API e regras de negócio', 'ABERTA', 'ALTA', '2025-10-01', '2025-11-30', 200.00, 'João Silva', 1, 'admin'),
('Testes Automatizados', 'Criação da suite de testes', 'ABERTA', 'MEDIA', '2025-11-01', '2025-12-15', 60.00, 'Teste QA', 1, 'admin'),
('Análise Base de Dados', 'Mapeamento da estrutura atual', 'EM_ANDAMENTO', 'CRITICA', '2025-10-01', '2025-10-15', 40.00, 'DBA Team', 2, 'admin');

-- Triggers para auditoria automática
DELIMITER //

CREATE TRIGGER tr_projeto_status_audit
AFTER UPDATE ON projeto
FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status THEN
        INSERT INTO auditoria_status (tabela, id_registro, status_anterior, status_novo, usuario)
        VALUES ('PROJETO', NEW.id, OLD.status, NEW.status, NEW.usuario_atualizacao);
    END IF;
END//

CREATE TRIGGER tr_tarefa_status_audit
AFTER UPDATE ON tarefa
FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status THEN
        INSERT INTO auditoria_status (tabela, id_registro, status_anterior, status_novo, usuario)
        VALUES ('TAREFA', NEW.id, OLD.status, NEW.status, NEW.usuario_atualizacao);
    END IF;
END//

DELIMITER ;

-- Stored Procedures úteis
DELIMITER //

CREATE PROCEDURE sp_relatorio_projeto(IN projeto_id BIGINT)
BEGIN
    SELECT 
        p.*,
        COUNT(t.id) as total_tarefas,
        COUNT(CASE WHEN t.status = 'CONCLUIDA' THEN 1 END) as tarefas_concluidas,
        SUM(COALESCE(t.estimativa_horas, 0)) as total_horas_estimadas,
        SUM(COALESCE(t.horas_trabalhadas, 0)) as total_horas_trabalhadas
    FROM projeto p
    LEFT JOIN tarefa t ON p.id = t.id_projeto AND t.ativo = TRUE
    WHERE p.id = projeto_id AND p.ativo = TRUE
    GROUP BY p.id;
    
    SELECT * FROM tarefa WHERE id_projeto = projeto_id AND ativo = TRUE ORDER BY prioridade DESC, data_fim_prevista;
END//

DELIMITER ;
