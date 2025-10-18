import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '@environments/environment';
import { ApiError } from '@app/models';

/**
 * Service base para consumo da API REST
 * Fornece métodos comuns para HTTP requests
 */
@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  /**
   * GET request
   */
  get<T>(endpoint: string, params?: HttpParams): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}/${endpoint}`, { params })
      .pipe(catchError(this.handleError));
  }

  /**
   * POST request
   */
  post<T>(endpoint: string, data: any): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}/${endpoint}`, data)
      .pipe(catchError(this.handleError));
  }

  /**
   * PUT request
   */
  put<T>(endpoint: string, data: any): Observable<T> {
    return this.http.put<T>(`${this.baseUrl}/${endpoint}`, data)
      .pipe(catchError(this.handleError));
  }

  /**
   * PATCH request
   */
  patch<T>(endpoint: string, data?: any): Observable<T> {
    return this.http.patch<T>(`${this.baseUrl}/${endpoint}`, data)
      .pipe(catchError(this.handleError));
  }

  /**
   * DELETE request
   */
  delete<T>(endpoint: string): Observable<T> {
    return this.http.delete<T>(`${this.baseUrl}/${endpoint}`)
      .pipe(catchError(this.handleError));
  }

  /**
   * Cria HttpParams a partir de um objeto
   */
  createParams(filters: any): HttpParams {
    let params = new HttpParams();
    
    Object.keys(filters).forEach(key => {
      const value = filters[key];
      if (value !== null && value !== undefined && value !== '') {
        params = params.set(key, value.toString());
      }
    });
    
    return params;
  }

  /**
   * Tratamento centralizado de erros HTTP
   */
  private handleError = (error: HttpErrorResponse): Observable<never> => {
    let apiError: ApiError;

    if (error.error instanceof ErrorEvent) {
      // Erro do cliente ou rede
      apiError = {
        timestamp: new Date().toISOString(),
        status: 0,
        error: 'Erro de Rede',
        message: 'Erro de conexão. Verifique sua internet.',
        path: ''
      };
    } else {
      // Erro do servidor
      apiError = error.error || {
        timestamp: new Date().toISOString(),
        status: error.status,
        error: error.statusText,
        message: error.message || 'Erro desconhecido',
        path: error.url || ''
      };
    }

    console.error('Erro da API:', apiError);
    return throwError(() => apiError);
  };
}
