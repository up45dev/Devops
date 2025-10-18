import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import {
  ProjetoResponse,
  ProjetoRequest,
  ProjetoFiltros,
  PageResponse
} from '@app/models';

/**
 * Service para gerenciamento de Projetos
 */
@Injectable({
  providedIn: 'root'
})
export class ProjetoService {
  private readonly endpoint = 'projetos';

  constructor(private apiService: ApiService) {}

  /**
   * Lista projetos com paginação e filtros
   */
  listar(filtros: ProjetoFiltros = {}): Observable<PageResponse<ProjetoResponse>> {
    const params = this.apiService.createParams(filtros);
    return this.apiService.get<PageResponse<ProjetoResponse>>(this.endpoint, params);
  }

  /**
   * Lista todos os projetos (sem paginação) para seleção
   */
  listarTodos(): Observable<PageResponse<ProjetoResponse>> {
    const params = this.apiService.createParams({ size: 1000 });
    return this.apiService.get<PageResponse<ProjetoResponse>>(this.endpoint, params);
  }

  /**
   * Busca projeto por ID
   */
  buscarPorId(id: number): Observable<ProjetoResponse> {
    return this.apiService.get<ProjetoResponse>(`${this.endpoint}/${id}`);
  }

  /**
   * Cria novo projeto
   */
  criar(projeto: ProjetoRequest): Observable<ProjetoResponse> {
    return this.apiService.post<ProjetoResponse>(this.endpoint, projeto);
  }

  /**
   * Atualiza projeto existente
   */
  atualizar(id: number, projeto: ProjetoRequest): Observable<ProjetoResponse> {
    return this.apiService.put<ProjetoResponse>(`${this.endpoint}/${id}`, projeto);
  }

  /**
   * Exclui projeto
   */
  excluir(id: number): Observable<void> {
    return this.apiService.delete<void>(`${this.endpoint}/${id}`);
  }

  /**
   * Altera status do projeto
   */
  alterarStatus(id: number, status: string): Observable<ProjetoResponse> {
    const params = this.apiService.createParams({ status });
    return this.apiService.patch<ProjetoResponse>(`${this.endpoint}/${id}/status`, null);
  }

  /**
   * Busca projetos atrasados
   */
  buscarAtrasados(): Observable<ProjetoResponse[]> {
    return this.apiService.get<ProjetoResponse[]>(`${this.endpoint}/atrasados`);
  }

  /**
   * Busca projetos por responsável
   */
  buscarPorResponsavel(responsavel: string): Observable<ProjetoResponse[]> {
    return this.apiService.get<ProjetoResponse[]>(`${this.endpoint}/responsavel/${responsavel}`);
  }

  /**
   * Obtém estatísticas dos projetos
   */
  obterEstatisticas(): Observable<any> {
    return this.apiService.get<any>(`${this.endpoint}/estatisticas`);
  }
}
