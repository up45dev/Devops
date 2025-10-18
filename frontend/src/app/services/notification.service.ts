import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Notificacao } from '@app/models';
import { environment } from '@environments/environment';

/**
 * Service para exibição de notificações
 */
@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private snackBar: MatSnackBar) {}

  /**
   * Exibe notificação de sucesso
   */
  success(mensagem: string, titulo?: string): void {
    this.show({
      tipo: 'success',
      titulo: titulo || 'Sucesso',
      mensagem,
      duracao: environment.notification.defaultDuration
    });
  }

  /**
   * Exibe notificação de erro
   */
  error(mensagem: string, titulo?: string): void {
    this.show({
      tipo: 'error',
      titulo: titulo || 'Erro',
      mensagem,
      duracao: environment.notification.errorDuration
    });
  }

  /**
   * Exibe notificação de warning
   */
  warning(mensagem: string, titulo?: string): void {
    this.show({
      tipo: 'warning',
      titulo: titulo || 'Atenção',
      mensagem,
      duracao: environment.notification.defaultDuration
    });
  }

  /**
   * Exibe notificação de informação
   */
  info(mensagem: string, titulo?: string): void {
    this.show({
      tipo: 'info',
      titulo: titulo || 'Informação',
      mensagem,
      duracao: environment.notification.defaultDuration
    });
  }

  /**
   * Exibe notificação customizada
   */
  private show(notificacao: Notificacao): void {
    const message = notificacao.titulo ? 
      `${notificacao.titulo}: ${notificacao.mensagem}` : 
      notificacao.mensagem;

    this.snackBar.open(message, 'Fechar', {
      duration: notificacao.duracao || environment.notification.defaultDuration,
      panelClass: [`snackbar-${notificacao.tipo}`],
      horizontalPosition: 'end',
      verticalPosition: 'top'
    });
  }
}
