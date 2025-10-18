import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

export interface ConfirmDialogData {
  titulo: string;
  mensagem: string;
  textoBotaoConfirmar?: string;
  textoBotaoCancelar?: string;
  corBotaoConfirmar?: 'primary' | 'accent' | 'warn';
}

/**
 * Componente para diálogo de confirmação
 */
@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule
  ],
  template: `
    <div class="confirm-dialog">
      <h2 mat-dialog-title>
        <mat-icon>{{ data.corBotaoConfirmar === 'warn' ? 'warning' : 'help' }}</mat-icon>
        {{ data.titulo }}
      </h2>
      
      <mat-dialog-content>
        <p>{{ data.mensagem }}</p>
      </mat-dialog-content>
      
      <mat-dialog-actions align="end">
        <button mat-button 
                (click)="onCancel()"
                cdkFocusInitial>
          {{ data.textoBotaoCancelar || 'Cancelar' }}
        </button>
        
        <button mat-raised-button 
                [color]="data.corBotaoConfirmar || 'primary'"
                (click)="onConfirm()">
          {{ data.textoBotaoConfirmar || 'Confirmar' }}
        </button>
      </mat-dialog-actions>
    </div>
  `,
  styles: [`
    .confirm-dialog {
      min-width: 300px;
      
      h2 {
        display: flex;
        align-items: center;
        gap: 8px;
        margin: 0;
        
        mat-icon {
          font-size: 24px;
        }
      }
      
      mat-dialog-content {
        margin: 16px 0;
        
        p {
          margin: 0;
          line-height: 1.5;
        }
      }
      
      mat-dialog-actions {
        gap: 8px;
        
        button {
          min-width: 80px;
        }
      }
    }
  `]
})
export class ConfirmDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ConfirmDialogData
  ) {}

  onConfirm(): void {
    this.dialogRef.close(true);
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
