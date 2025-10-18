import { Component, OnInit, Output, EventEmitter, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { Observable, startWith, map } from 'rxjs';

import { 
  TarefaRequest, 
  TarefaResponse, 
  ProjetoResponse, 
  StatusTarefa, 
  Prioridade,
  StatusTarefaLabels,
  PrioridadeLabels
} from '@app/models';
import { TarefaService } from '@app/services/tarefa.service';
import { ProjetoService } from '@app/services/projeto.service';
import { NotificationService } from '@app/services/notification.service';

/**
 * Componente para formulário de cadastro/edição de tarefas
 */
@Component({
  selector: 'app-tarefa-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCardModule,
    MatIconModule,
    MatProgressBarModule
  ],
  templateUrl: './tarefa-form.component.html',
  styleUrls: ['./tarefa-form.component.scss']
})
export class TarefaFormComponent implements OnInit, OnChanges {
  @Input() tarefa?: TarefaResponse;
  @Input() projetoSelecionado?: number;
  @Output() tarefaSalva = new EventEmitter<TarefaResponse>();
  @Output() cancelar = new EventEmitter<void>();

  form!: FormGroup;
  projetos: ProjetoResponse[] = [];
  isLoading = false;
  isSubmitting = false;

  // Enums para o template
  statusTarefaOptions = Object.entries(StatusTarefaLabels).map(([key, value]) => ({
    value: key as StatusTarefa,
    label: value
  }));

  prioridadeOptions = Object.entries(PrioridadeLabels).map(([key, value]) => ({
    value: key as Prioridade,
    label: value
  }));

  constructor(
    private fb: FormBuilder,
    private tarefaService: TarefaService,
    private projetoService: ProjetoService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.carregarProjetos();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['tarefa'] && this.form) {
      this.populateForm();
    }
    if (changes['projetoSelecionado'] && this.form) {
      this.form.patchValue({ idProjeto: this.projetoSelecionado });
    }
  }

  /**
   * Inicializa o formulário
   */
  private initForm(): void {
    this.form = this.fb.group({
      titulo: ['', [Validators.required, Validators.maxLength(255)]],
      descricao: [''],
      status: [StatusTarefa.ABERTA],
      prioridade: [Prioridade.MEDIA],
      dataInicio: [''],
      dataFimPrevista: [''],
      estimativaHoras: ['', [Validators.min(0)]],
      horasTrabalhadas: [0, [Validators.min(0)]],
      percentualConclusao: [0, [Validators.min(0), Validators.max(100)]],
      responsavel: ['', [Validators.maxLength(255)]],
      observacoes: [''],
      idProjeto: ['', Validators.required]
    });

    // Define projeto selecionado se fornecido
    if (this.projetoSelecionado) {
      this.form.patchValue({ idProjeto: this.projetoSelecionado });
    }
  }

  /**
   * Popula o formulário com dados da tarefa para edição
   */
  private populateForm(): void {
    if (this.tarefa) {
      this.form.patchValue({
        titulo: this.tarefa.titulo,
        descricao: this.tarefa.descricao || '',
        status: this.tarefa.status,
        prioridade: this.tarefa.prioridade,
        dataInicio: this.tarefa.dataInicio || '',
        dataFimPrevista: this.tarefa.dataFimPrevista || '',
        estimativaHoras: this.tarefa.estimativaHoras || 0,
        horasTrabalhadas: this.tarefa.horasTrabalhadas || 0,
        percentualConclusao: this.tarefa.percentualConclusao || 0,
        responsavel: this.tarefa.responsavel || '',
        observacoes: this.tarefa.observacoes || '',
        idProjeto: this.tarefa.idProjeto
      });
    }
  }

  /**
   * Carrega lista de projetos para seleção
   */
  private carregarProjetos(): void {
    this.isLoading = true;
    
    this.projetoService.listarTodos().subscribe({
      next: (response) => {
        this.projetos = response.content;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erro ao carregar projetos:', error);
        this.isLoading = false;
      }
    });
  }

  /**
   * Submete o formulário
   */
  onSubmit(): void {
    if (this.form.valid) {
      this.isSubmitting = true;
      const tarefaData: TarefaRequest = this.form.value;

      // Converte datas para string no formato correto
      if (tarefaData.dataInicio) {
        tarefaData.dataInicio = this.formatDate(tarefaData.dataInicio);
      }
      if (tarefaData.dataFimPrevista) {
        tarefaData.dataFimPrevista = this.formatDate(tarefaData.dataFimPrevista);
      }

      const operation = this.tarefa ? 
        this.tarefaService.atualizar(this.tarefa.id, tarefaData) :
        this.tarefaService.criar(tarefaData);

      operation.subscribe({
        next: (tarefa) => {
          this.isSubmitting = false;
          const acao = this.tarefa ? 'atualizada' : 'criada';
          this.notificationService.success(`Tarefa ${acao} com sucesso!`);
          this.tarefaSalva.emit(tarefa);
          this.resetForm();
        },
        error: (error) => {
          console.error('Erro ao salvar tarefa:', error);
          this.isSubmitting = false;
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  /**
   * Cancela a operação
   */
  onCancel(): void {
    this.resetForm();
    this.cancelar.emit();
  }

  /**
   * Reseta o formulário
   */
  private resetForm(): void {
    this.form.reset();
    this.form.patchValue({
      status: StatusTarefa.ABERTA,
      prioridade: Prioridade.MEDIA,
      horasTrabalhadas: 0,
      percentualConclusao: 0,
      idProjeto: this.projetoSelecionado || ''
    });
    this.tarefa = undefined;
  }

  /**
   * Marca todos os campos como touched para mostrar erros de validação
   */
  private markFormGroupTouched(): void {
    Object.keys(this.form.controls).forEach(key => {
      const control = this.form.get(key);
      control?.markAsTouched();
    });
  }

  /**
   * Formata data para string (YYYY-MM-DD)
   */
  private formatDate(date: any): string {
    if (date instanceof Date) {
      return date.toISOString().split('T')[0];
    }
    return date;
  }

  /**
   * Verifica se um campo tem erro
   */
  hasError(fieldName: string, errorType?: string): boolean {
    const field = this.form.get(fieldName);
    if (errorType) {
      return field ? field.hasError(errorType) && field.touched : false;
    }
    return field ? field.invalid && field.touched : false;
  }

  /**
   * Obtém mensagem de erro para um campo
   */
  getErrorMessage(fieldName: string): string {
    const field = this.form.get(fieldName);
    if (field?.errors && field.touched) {
      if (field.errors['required']) {
        return 'Este campo é obrigatório';
      }
      if (field.errors['maxlength']) {
        return `Máximo ${field.errors['maxlength'].requiredLength} caracteres`;
      }
      if (field.errors['min']) {
        return `Valor mínimo: ${field.errors['min'].min}`;
      }
      if (field.errors['max']) {
        return `Valor máximo: ${field.errors['max'].max}`;
      }
    }
    return '';
  }

  /**
   * Obtém nome do projeto selecionado
   */
  getNomeProjeto(id: number): string {
    const projeto = this.projetos.find(p => p.id === id);
    return projeto ? projeto.nome : '';
  }
}
