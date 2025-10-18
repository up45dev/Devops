// Enumerações
export enum StatusProjeto {
  PLANEJAMENTO = 'PLANEJAMENTO',
  EM_ANDAMENTO = 'EM_ANDAMENTO',
  PAUSADO = 'PAUSADO',
  CONCLUIDO = 'CONCLUIDO',
  CANCELADO = 'CANCELADO'
}

export enum StatusTarefa {
  ABERTA = 'ABERTA',
  EM_ANDAMENTO = 'EM_ANDAMENTO',
  EM_REVISAO = 'EM_REVISAO',
  CONCLUIDA = 'CONCLUIDA',
  CANCELADA = 'CANCELADA'
}

export enum Prioridade {
  BAIXA = 'BAIXA',
  MEDIA = 'MEDIA',
  ALTA = 'ALTA',
  CRITICA = 'CRITICA'
}

// Interfaces para Projeto
export interface ProjetoRequest {
  nome: string;
  descricao?: string;
  dataInicio?: string;
  dataFimPrevista?: string;
  status?: StatusProjeto;
  prioridade?: Prioridade;
  orcamento?: number;
  responsavel?: string;
}

export interface ProjetoResponse {
  id: number;
  nome: string;
  descricao?: string;
  dataInicio?: string;
  dataFimPrevista?: string;
  dataFimReal?: string;
  status: StatusProjeto;
  prioridade: Prioridade;
  orcamento?: number;
  responsavel?: string;
  dataCriacao: string;
  dataAtualizacao: string;
  usuarioCriacao?: string;
  ativo: boolean;
  
  // Campos calculados
  percentualConclusao?: number;
  atrasado?: boolean;
  totalTarefas?: number;
  tarefasConcluidas?: number;
  totalHorasEstimadas?: number;
  totalHorasTrabalhadas?: number;
}

// Interfaces para Tarefa
export interface TarefaRequest {
  titulo: string;
  descricao?: string;
  status?: StatusTarefa;
  prioridade?: Prioridade;
  dataInicio?: string;
  dataFimPrevista?: string;
  estimativaHoras?: number;
  horasTrabalhadas?: number;
  percentualConclusao?: number;
  responsavel?: string;
  observacoes?: string;
  idProjeto: number;
}

export interface TarefaResponse {
  id: number;
  titulo: string;
  descricao?: string;
  status: StatusTarefa;
  prioridade: Prioridade;
  dataCriacao: string;
  dataInicio?: string;
  dataFimPrevista?: string;
  dataFimReal?: string;
  estimativaHoras?: number;
  horasTrabalhadas?: number;
  percentualConclusao: number;
  responsavel?: string;
  observacoes?: string;
  dataAtualizacao: string;
  ativo: boolean;
  
  // Informações do projeto
  idProjeto: number;
  nomeProjeto?: string;
  
  // Campos calculados
  atrasada?: boolean;
  diasRestantes?: number;
}

// Interface para paginação
export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      empty: boolean;
      sorted: boolean;
      unsorted: boolean;
    };
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  last: boolean;
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}

// Interface para filtros
export interface TarefaFiltros {
  projetoId?: number;
  titulo?: string;
  status?: StatusTarefa;
  responsavel?: string;
  prioridade?: Prioridade;
  page?: number;
  size?: number;
}

export interface ProjetoFiltros {
  nome?: string;
  status?: StatusProjeto;
  responsavel?: string;
  page?: number;
  size?: number;
}

// Interface para mensagens de erro
export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  validationErrors?: { [key: string]: string };
}

// Interface para notificações
export interface Notificacao {
  tipo: 'success' | 'error' | 'warning' | 'info';
  titulo: string;
  mensagem: string;
  duracao?: number;
}

// Utilitários para labels
export const StatusProjetoLabels: { [key in StatusProjeto]: string } = {
  [StatusProjeto.PLANEJAMENTO]: 'Planejamento',
  [StatusProjeto.EM_ANDAMENTO]: 'Em Andamento',
  [StatusProjeto.PAUSADO]: 'Pausado',
  [StatusProjeto.CONCLUIDO]: 'Concluído',
  [StatusProjeto.CANCELADO]: 'Cancelado'
};

export const StatusTarefaLabels: { [key in StatusTarefa]: string } = {
  [StatusTarefa.ABERTA]: 'Aberta',
  [StatusTarefa.EM_ANDAMENTO]: 'Em Andamento',
  [StatusTarefa.EM_REVISAO]: 'Em Revisão',
  [StatusTarefa.CONCLUIDA]: 'Concluída',
  [StatusTarefa.CANCELADA]: 'Cancelada'
};

export const PrioridadeLabels: { [key in Prioridade]: string } = {
  [Prioridade.BAIXA]: 'Baixa',
  [Prioridade.MEDIA]: 'Média',
  [Prioridade.ALTA]: 'Alta',
  [Prioridade.CRITICA]: 'Crítica'
};

export const PrioridadeCores: { [key in Prioridade]: string } = {
  [Prioridade.BAIXA]: '#4CAF50',
  [Prioridade.MEDIA]: '#FF9800',
  [Prioridade.ALTA]: '#F44336',
  [Prioridade.CRITICA]: '#9C27B0'
};

export const StatusTarefaCores: { [key in StatusTarefa]: string } = {
  [StatusTarefa.ABERTA]: '#9E9E9E',
  [StatusTarefa.EM_ANDAMENTO]: '#2196F3',
  [StatusTarefa.EM_REVISAO]: '#FF9800',
  [StatusTarefa.CONCLUIDA]: '#4CAF50',
  [StatusTarefa.CANCELADA]: '#F44336'
};
