import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { NotificationService } from '../services/notification.service';
import { environment } from '@environments/environment';
import { ApiError } from '@app/models';

/**
 * Interceptor HTTP para:
 * - Adicionar headers padrão
 * - Tratar erros globalmente
 * - Exibir notificações de erro
 */
@Injectable()
export class HttpInterceptorService implements HttpInterceptor {

  constructor(private notificationService: NotificationService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Adiciona headers padrão
    const authReq = req.clone({
      setHeaders: environment.defaultHeaders
    });

    return next.handle(authReq).pipe(
      catchError((error: HttpErrorResponse) => {
        this.handleError(error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Trata erros HTTP e exibe notificações apropriadas
   */
  private handleError(error: HttpErrorResponse): void {
    let mensagem = 'Erro desconhecido';
    let titulo = 'Erro';

    if (error.error instanceof ErrorEvent) {
      // Erro do cliente
      titulo = 'Erro de Conexão';
      mensagem = 'Verifique sua conexão com a internet';
    } else {
      // Erro do servidor
      const apiError: ApiError = error.error;
      
      switch (error.status) {
        case 400:
          titulo = 'Dados Inválidos';
          mensagem = this.formatValidationErrors(apiError);
          break;
        case 401:
          titulo = 'Não Autorizado';
          mensagem = 'Você não tem permissão para acessar este recurso';
          break;
        case 403:
          titulo = 'Acesso Negado';
          mensagem = 'Você não tem permissão para esta operação';
          break;
        case 404:
          titulo = 'Não Encontrado';
          mensagem = apiError?.message || 'Recurso não encontrado';
          break;
        case 500:
          titulo = 'Erro do Servidor';
          mensagem = 'Ocorreu um erro interno. Tente novamente mais tarde';
          break;
        default:
          titulo = 'Erro';
          mensagem = apiError?.message || `Erro ${error.status}: ${error.statusText}`;
      }
    }

    this.notificationService.error(mensagem, titulo);
  }

  /**
   * Formata erros de validação para exibição
   */
  private formatValidationErrors(apiError: ApiError): string {
    if (apiError?.validationErrors) {
      const errors = Object.entries(apiError.validationErrors)
        .map(([field, message]) => `${field}: ${message}`)
        .join('; ');
      return errors;
    }
    return apiError?.message || 'Dados inválidos fornecidos';
  }
}
