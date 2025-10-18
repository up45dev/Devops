import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

import { 
  TarefaResponse, 
  TarefaFiltros, 
  ProjetoResponse,
  StatusTarefa, 
  Prioridade,
  PageResponse,
  StatusTarefaLabels,
  PrioridadeLabels,
  StatusTarefaCores,
  PrioridadeCores
} from '@app/models';
import { TarefaService } from '@app/services/tarefa.service';
import { ProjetoService } from '@app/services/projeto.service';
import { NotificationService } from '@app/services/notification.service';
import { environment } from '@environments/environment';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';

/**
 * Componente para listagem de tarefas com grid, filtros e paginação
 */
@Component({
  selector: 'app-tarefa-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatChipsModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatMenuModule
  ],
  templateUrl: './tarefa-list.component.html',
  styleUrls: ['./tarefa-list.component.scss']
})
export class TarefaListComponent implements OnInit, OnChanges {
  @Input() projetoSelecionado?: number;
  @Output() editarTarefa = new EventEmitter<TarefaResponse>();
  @Output() tarefaExcluida = new EventEmitter<number>();

  // Dados
  tarefas: TarefaResponse[] = [];
  projetos: ProjetoResponse[] = [];
  totalElements = 0;
  isLoading = false;

  // Formulário de filtros
  filtrosForm!: FormGroup;
  
  // Paginação
  pageSize = environment.pagination.defaultPageSize;
  pageSizeOptions = environment.pagination.pageSizeOptions;
  currentPage = 0;

  // Colunas da tabela
  displayedColumns: string[] = [
    'titulo',
    'projeto', 
    'status',
    'prioridade',
    'responsavel',
    'dataFimPrevista',
    'percentualConclusao',
    'acoes'
  ];

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
    private notificationService: NotificationService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.initFiltrosForm();
    this.carregarProjetos();
    this.carregarTarefas();
    this.setupFiltrosWatcher();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['projetoSelecionado']) {
      this.filtrosForm?.patchValue({ projetoId: this.projetoSelecionado });
      this.carregarTarefas();
    }
  }

  /**
   * Inicializa o formulário de filtros
   */
  private initFiltrosForm(): void {
    this.filtrosForm = this.fb.group({
      projetoId: [this.projetoSelecionado || ''],
      titulo: [''],
      status: [''],
      prioridade: [''],
      responsavel: ['']
    });
  }

  /**
   * Configura observador para filtros
   */
  private setupFiltrosWatcher(): void {
    this.filtrosForm.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged()
      )
      .subscribe(() => {
        this.currentPage = 0;
        this.carregarTarefas();
      });
  }

  /**
   * Carrega lista de projetos para filtro
   */
  private carregarProjetos(): void {
    this.projetoService.listarTodos().subscribe({
      next: (response) => {
        this.projetos = response.content;
      },
      error: (error) => {
        console.error('Erro ao carregar projetos:', error);
      }
    });
  }

  /**
   * Carrega lista de tarefas
   */
  carregarTarefas(): void {
    this.isLoading = true;
    
    const filtros: TarefaFiltros = {
      ...this.getFiltrosAtivos(),
      page: this.currentPage,
      size: this.pageSize
    };

    this.tarefaService.listar(filtros).subscribe({
      next: (response: PageResponse<TarefaResponse>) => {
        this.tarefas = response.content;
        this.totalElements = response.totalElements;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erro ao carregar tarefas:', error);
        this.isLoading = false;
      }
    });
  }

  /**
   * Obtém filtros ativos do formulário
   */
  private getFiltrosAtivos(): TarefaFiltros {
    const filtros = this.filtrosForm.value;
    const filtrosLimpos: TarefaFiltros = {};
    
    Object.keys(filtros).forEach(key => {
      const value = filtros[key];
      if (value !== null && value !== undefined && value !== '') {
        (filtrosLimpos as any)[key] = value;
      }
    });
    
    return filtrosLimpos;
  }

  /**
   * Manipula eventos de paginação
   */
  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.carregarTarefas();
  }

  /**
   * Limpa todos os filtros
   */
  limparFiltros(): void {
    this.filtrosForm.reset();
    if (this.projetoSelecionado) {
      this.filtrosForm.patchValue({ projetoId: this.projetoSelecionado });
    }
  }

  /**
   * Edita uma tarefa
   */
  onEditarTarefa(tarefa: TarefaResponse): void {
    this.editarTarefa.emit(tarefa);
  }

  /**
   * Exclui uma tarefa
   */
  onExcluirTarefa(tarefa: TarefaResponse): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        titulo: 'Confirmar Exclusão',
        mensagem: `Deseja realmente excluir a tarefa "${tarefa.titulo}"?`,
        textoBotaoConfirmar: 'Excluir',
        textoBotaoCancelar: 'Cancelar',
        corBotaoConfirmar: 'warn'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.excluirTarefa(tarefa.id);
      }
    });
  }

  /**
   * Executa a exclusão da tarefa
   */
  private excluirTarefa(id: number): void {
    this.tarefaService.excluir(id).subscribe({
      next: () => {
        this.notificationService.success('Tarefa excluída com sucesso!');
        this.tarefaExcluida.emit(id);
        this.carregarTarefas();
      },
      error: (error) => {
        console.error('Erro ao excluir tarefa:', error);
      }
    });
  }

  /**
   * Atualiza status da tarefa
   */
  onAlterarStatus(tarefa: TarefaResponse, novoStatus: StatusTarefa): void {
    this.tarefaService.alterarStatus(tarefa.id, novoStatus).subscribe({
      next: () => {
        this.notificationService.success('Status atualizado com sucesso!');
        this.carregarTarefas();
      },
      error: (error) => {
        console.error('Erro ao alterar status:', error);
      }
    });
  }

  /**
   * Formata data para exibição
   */
  formatarData(data: string | undefined): string {
    if (!data) return '-';
    
    try {
      const date = new Date(data);
      return date.toLocaleDateString('pt-BR');
    } catch {
      return data;
    }
  }

  /**
   * Verifica se a tarefa está atrasada
   */
  isTarefaAtrasada(tarefa: TarefaResponse): boolean {
    if (!tarefa.dataFimPrevista) return false;
    
    const hoje = new Date();
    const dataFim = new Date(tarefa.dataFimPrevista);
    
    return dataFim < hoje && 
           tarefa.status !== StatusTarefa.CONCLUIDA && 
           tarefa.status !== StatusTarefa.CANCELADA;
  }

  /**
   * Obtém cor do status
   */
  getCorStatus(status: StatusTarefa): string {
    return StatusTarefaCores[status] || '#9E9E9E';
  }

  /**
   * Obtém cor da prioridade
   */
  getCorPrioridade(prioridade: Prioridade): string {
    return PrioridadeCores[prioridade] || '#9E9E9E';
  }

  /**
   * Obtém label do status
   */
  getLabelStatus(status: StatusTarefa): string {
    return StatusTarefaLabels[status] || status;
  }

  /**
   * Obtém label da prioridade
   */
  getLabelPrioridade(prioridade: Prioridade): string {
    return PrioridadeLabels[prioridade] || prioridade;
  }

  /**
   * Obtém nome do projeto
   */
  getNomeProjeto(id: number): string {
    const projeto = this.projetos.find(p => p.id === id);
    return projeto ? projeto.nome : 'Projeto não encontrado';
  }
}
