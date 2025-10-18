-- =============================================================================
-- PARTE 3 - Exemplos de Consultas SQL (Sintaxe compatível com DB2)
-- Sistema de Gestão de Tarefas
-- Autor: MiniMax Agent
-- Data: 2025-09-28
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. CONSULTA COM PAGINAÇÃO
-- Listar tarefas com paginação usando OFFSET e FETCH (sintaxe DB2)
-- -----------------------------------------------------------------------------

-- Exemplo: Página 2, com 5 tarefas por página (OFFSET 5, FETCH 5)
SELECT 
    t.ID,
    t.TITULO,
    t.DESCRICAO,
    t.STATUS,
    t.DATA_CRIACAO,
    p.NOME AS NOME_PROJETO
FROM TAREFA t
INNER JOIN PROJETO p ON t.ID_PROJETO = p.ID
WHERE t.ATIVO = 1
ORDER BY t.DATA_CRIACAO DESC
OFFSET 5 ROWS FETCH NEXT 5 ROWS ONLY;

-- Consulta para contar total de registros (para cálculo de páginas)
SELECT COUNT(*) AS TOTAL_TAREFAS
FROM TAREFA t
WHERE t.ATIVO = 1;

-- Exemplo com filtro por projeto + paginação
SELECT 
    t.ID,
    t.TITULO,
    t.DESCRICAO,
    t.STATUS,
    t.DATA_CRIACAO,
    p.NOME AS NOME_PROJETO
FROM TAREFA t
INNER JOIN PROJETO p ON t.ID_PROJETO = p.ID
WHERE t.ATIVO = 1 
  AND t.ID_PROJETO = 1  -- Filtro por projeto específico
ORDER BY t.DATA_CRIACAO DESC
OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY;

-- -----------------------------------------------------------------------------
-- 2. CONSULTA COM JOIN ENTRE TAREFA E PROJETO
-- Busca detalhada com informações completas de tarefas e projetos
-- -----------------------------------------------------------------------------

-- Join completo com todas as informações relevantes
SELECT 
    t.ID AS TAREFA_ID,
    t.TITULO AS TAREFA_TITULO,
    t.DESCRICAO AS TAREFA_DESCRICAO,
    t.STATUS AS TAREFA_STATUS,
    t.DATA_CRIACAO AS TAREFA_DATA_CRIACAO,
    t.DATA_INICIO AS TAREFA_DATA_INICIO,
    t.DATA_FIM_PREVISTA AS TAREFA_DATA_FIM_PREVISTA,
    t.ESTIMATIVA_HORAS AS TAREFA_ESTIMATIVA_HORAS,
    t.PERCENTUAL_CONCLUSAO AS TAREFA_PERCENTUAL,
    t.RESPONSAVEL AS TAREFA_RESPONSAVEL,
    --
    p.ID AS PROJETO_ID,
    p.NOME AS PROJETO_NOME,
    p.DESCRICAO AS PROJETO_DESCRICAO,
    p.STATUS AS PROJETO_STATUS,
    p.PRIORIDADE AS PROJETO_PRIORIDADE,
    p.DATA_INICIO AS PROJETO_DATA_INICIO,
    p.DATA_FIM_PREVISTA AS PROJETO_DATA_FIM_PREVISTA,
    p.RESPONSAVEL AS PROJETO_RESPONSAVEL
FROM TAREFA t
INNER JOIN PROJETO p ON t.ID_PROJETO = p.ID
WHERE t.ATIVO = 1 
  AND p.ATIVO = 1
ORDER BY p.NOME ASC, t.DATA_CRIACAO DESC;

-- Join com contagem de tarefas por projeto
SELECT 
    p.ID AS PROJETO_ID,
    p.NOME AS PROJETO_NOME,
    p.STATUS AS PROJETO_STATUS,
    COUNT(t.ID) AS TOTAL_TAREFAS,
    COUNT(CASE WHEN t.STATUS = 'CONCLUIDA' THEN 1 END) AS TAREFAS_CONCLUIDAS,
    COUNT(CASE WHEN t.STATUS = 'ABERTA' THEN 1 END) AS TAREFAS_ABERTAS,
    COUNT(CASE WHEN t.STATUS = 'EM_ANDAMENTO' THEN 1 END) AS TAREFAS_EM_ANDAMENTO
FROM PROJETO p
LEFT JOIN TAREFA t ON p.ID = t.ID_PROJETO AND t.ATIVO = 1
WHERE p.ATIVO = 1
GROUP BY p.ID, p.NOME, p.STATUS
ORDER BY p.NOME ASC;

-- Join para identificar tarefas atrasadas
SELECT 
    t.ID AS TAREFA_ID,
    t.TITULO AS TAREFA_TITULO,
    t.STATUS AS TAREFA_STATUS,
    t.DATA_FIM_PREVISTA AS DATA_PREVISTA,
    p.NOME AS PROJETO_NOME,
    DAYS(CURRENT_DATE) - DAYS(t.DATA_FIM_PREVISTA) AS DIAS_ATRASO
FROM TAREFA t
INNER JOIN PROJETO p ON t.ID_PROJETO = p.ID
WHERE t.ATIVO = 1 
  AND p.ATIVO = 1
  AND t.DATA_FIM_PREVISTA < CURRENT_DATE
  AND t.STATUS NOT IN ('CONCLUIDA', 'CANCELADA')
ORDER BY DIAS_ATRASO DESC;

-- -----------------------------------------------------------------------------
-- 3. CONSULTA COM AGRUPAMENTO POR STATUS
-- Análise estatística das tarefas agrupadas por status
-- -----------------------------------------------------------------------------

-- Agrupamento básico por status das tarefas
SELECT 
    t.STATUS AS STATUS_TAREFA,
    COUNT(*) AS QUANTIDADE_TAREFAS,
    COUNT(*) * 100.0 / (SELECT COUNT(*) FROM TAREFA WHERE ATIVO = 1) AS PERCENTUAL,
    AVG(t.PERCENTUAL_CONCLUSAO) AS PERCENTUAL_MEDIO_CONCLUSAO,
    SUM(COALESCE(t.ESTIMATIVA_HORAS, 0)) AS TOTAL_HORAS_ESTIMADAS,
    SUM(COALESCE(t.HORAS_TRABALHADAS, 0)) AS TOTAL_HORAS_TRABALHADAS
FROM TAREFA t
WHERE t.ATIVO = 1
GROUP BY t.STATUS
ORDER BY QUANTIDADE_TAREFAS DESC;

-- Agrupamento por status com informações do projeto
SELECT 
    p.NOME AS PROJETO_NOME,
    t.STATUS AS STATUS_TAREFA,
    COUNT(*) AS QUANTIDADE_TAREFAS,
    AVG(t.PERCENTUAL_CONCLUSAO) AS PERCENTUAL_MEDIO_CONCLUSAO,
    MIN(t.DATA_CRIACAO) AS PRIMEIRA_TAREFA,
    MAX(t.DATA_CRIACAO) AS ULTIMA_TAREFA
FROM TAREFA t
INNER JOIN PROJETO p ON t.ID_PROJETO = p.ID
WHERE t.ATIVO = 1 AND p.ATIVO = 1
GROUP BY p.NOME, t.STATUS
ORDER BY p.NOME ASC, QUANTIDADE_TAREFAS DESC;

-- Agrupamento por status com filtro de período (últimos 30 dias)
SELECT 
    t.STATUS AS STATUS_TAREFA,
    COUNT(*) AS TAREFAS_ULTIMOS_30_DIAS,
    AVG(t.PERCENTUAL_CONCLUSAO) AS PERCENTUAL_MEDIO,
    COUNT(CASE WHEN t.PRIORIDADE = 'ALTA' THEN 1 END) AS TAREFAS_ALTA_PRIORIDADE,
    COUNT(CASE WHEN t.PRIORIDADE = 'CRITICA' THEN 1 END) AS TAREFAS_CRITICAS
FROM TAREFA t
WHERE t.ATIVO = 1
  AND t.DATA_CRIACAO >= CURRENT_DATE - 30 DAYS
GROUP BY t.STATUS
HAVING COUNT(*) > 0
ORDER BY TAREFAS_ULTIMOS_30_DIAS DESC;

-- Agrupamento combinado: Status + Prioridade
SELECT 
    t.STATUS AS STATUS_TAREFA,
    t.PRIORIDADE AS PRIORIDADE_TAREFA,
    COUNT(*) AS QUANTIDADE,
    AVG(t.PERCENTUAL_CONCLUSAO) AS PERCENTUAL_MEDIO_CONCLUSAO,
    AVG(DAYS(COALESCE(t.DATA_FIM_PREVISTA, CURRENT_DATE)) - DAYS(t.DATA_CRIACAO)) AS PRAZO_MEDIO_DIAS
FROM TAREFA t
WHERE t.ATIVO = 1
GROUP BY t.STATUS, t.PRIORIDADE
ORDER BY t.STATUS ASC, t.PRIORIDADE ASC;

-- -----------------------------------------------------------------------------
-- CONSULTAS EXTRAS PARA ANÁLISE DE NEGÓCIO
-- -----------------------------------------------------------------------------

-- Ranking de projetos por produtividade
SELECT 
    p.NOME AS PROJETO_NOME,
    COUNT(t.ID) AS TOTAL_TAREFAS,
    COUNT(CASE WHEN t.STATUS = 'CONCLUIDA' THEN 1 END) AS TAREFAS_CONCLUIDAS,
    CASE 
        WHEN COUNT(t.ID) > 0 THEN 
            (COUNT(CASE WHEN t.STATUS = 'CONCLUIDA' THEN 1 END) * 100.0 / COUNT(t.ID))
        ELSE 0 
    END AS PERCENTUAL_CONCLUSAO_PROJETO,
    AVG(t.PERCENTUAL_CONCLUSAO) AS PERCENTUAL_MEDIO_TAREFAS
FROM PROJETO p
LEFT JOIN TAREFA t ON p.ID = t.ID_PROJETO AND t.ATIVO = 1
WHERE p.ATIVO = 1
GROUP BY p.ID, p.NOME
ORDER BY PERCENTUAL_CONCLUSAO_PROJETO DESC, TOTAL_TAREFAS DESC;

-- Análise temporal: Tarefas criadas por mês
SELECT 
    YEAR(t.DATA_CRIACAO) AS ANO,
    MONTH(t.DATA_CRIACAO) AS MES,
    COUNT(*) AS TAREFAS_CRIADAS,
    COUNT(CASE WHEN t.STATUS = 'CONCLUIDA' THEN 1 END) AS TAREFAS_CONCLUIDAS,
    AVG(t.PERCENTUAL_CONCLUSAO) AS PERCENTUAL_MEDIO
FROM TAREFA t
WHERE t.ATIVO = 1
GROUP BY YEAR(t.DATA_CRIACAO), MONTH(t.DATA_CRIACAO)
ORDER BY ANO DESC, MES DESC;

-- =============================================================================
-- NOTAS SOBRE COMPATIBILIDADE DB2:
-- 
-- 1. PAGINAÇÃO: Utilizamos OFFSET ... ROWS FETCH NEXT ... ROWS ONLY (DB2 v9.7+)
-- 2. JOINS: Sintaxe ANSI padrão compatível com DB2
-- 3. FUNÇÕES: CURRENT_DATE, DAYS(), YEAR(), MONTH() são nativas do DB2
-- 4. CASE WHEN: Sintaxe padrão SQL compatível
-- 5. Comentários com -- são suportados
-- 6. GROUP BY com múltiplas colunas é padrão
-- 7. HAVING clause é suportada
-- 8. COALESCE é função padrão SQL
-- =============================================================================