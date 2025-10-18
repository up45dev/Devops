import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import {
  TarefaResponse,
  TarefaRequest,
  TarefaFiltros,
  PageResponse,
  StatusTarefa
} from '@app/models';

/**
 * Service para gerenciamento de Tarefas
 */
@Injectable({
  providedIn: 'root'
})
export class TarefaService {
  private readonly endpoint = 'tarefas';

  constructor(private apiService: ApiService) {}

  /**
   * Lista tarefas com paginação e filtros
   */
  listar(filtros: TarefaFiltros = {}): Observable<PageResponse<TarefaResponse>> {
    const params = this.apiService.createParams(filtros);
    return this.apiService.get<PageResponse<TarefaResponse>>(this.endpoint, params);
  }

  /**
   * Lista tarefas por projeto
   */
  listarPorProjeto(projetoId: number): Observable<TarefaResponse[]> {
    return this.apiService.get<TarefaResponse[]>(`${this.endpoint}/projeto/${projetoId}`);
  }

  /**
   * Busca tarefa por ID
   */
  buscarPorId(id: number): Observable<TarefaResponse> {
    return this.apiService.get<TarefaResponse>(`${this.endpoint}/${id}`);
  }

  /**
   * Cria nova tarefa
   */
  criar(tarefa: TarefaRequest): Observable<TarefaResponse> {
    return this.apiService.post<TarefaResponse>(this.endpoint, tarefa);
  }

  /**
   * Atualiza tarefa existente
   */
  atualizar(id: number, tarefa: TarefaRequest): Observable<TarefaResponse> {
    return this.apiService.put<TarefaResponse>(`${this.endpoint}/${id}`, tarefa);
  }

  /**
   * Exclui tarefa
   */
  excluir(id: number): Observable<void> {
    return this.apiService.delete<void>(`${this.endpoint}/${id}`);
  }

  /**
   * Altera status da tarefa
   */
  alterarStatus(id: number, status: StatusTarefa): Observable<TarefaResponse> {
    const params = this.apiService.createParams({ status });
    return this.apiService.patch<TarefaResponse>(`${this.endpoint}/${id}/status`, null);
  }

  /**
   * Atualiza percentual de conclusão
   */
  atualizarPercentual(id: number, percentual: number): Observable<TarefaResponse> {
    const params = this.apiService.createParams({ percentual });
    return this.apiService.patch<TarefaResponse>(`${this.endpoint}/${id}/percentual`, null);
  }

  /**
   * Busca tarefas atrasadas
   */
  buscarAtrasadas(): Observable<TarefaResponse[]> {
    return this.apiService.get<TarefaResponse[]>(`${this.endpoint}/atrasadas`);
  }

  /**
   * Busca tarefas que vencem em X dias
   */
  buscarVencendoEm(dias: number): Observable<TarefaResponse[]> {
    return this.apiService.get<TarefaResponse[]>(`${this.endpoint}/vencendo-em/${dias}`);
  }

  /**
   * Busca tarefas de alta prioridade
   */
  buscarAltaPrioridade(): Observable<TarefaResponse[]> {
    return this.apiService.get<TarefaResponse[]>(`${this.endpoint}/alta-prioridade`);
  }

  /**
   * Busca tarefas por responsável
   */
  buscarPorResponsavel(responsavel: string): Observable<TarefaResponse[]> {
    return this.apiService.get<TarefaResponse[]>(`${this.endpoint}/responsavel/${responsavel}`);
  }
}
