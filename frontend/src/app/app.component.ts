import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatCardModule } from '@angular/material/card';
import { MatTabsModule } from '@angular/material/tabs';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';

import { TarefaFormComponent } from './components/tarefa-form/tarefa-form.component';
import { TarefaListComponent } from './components/tarefa-list/tarefa-list.component';
import { TarefaResponse, ProjetoResponse } from './models';
import { ProjetoService } from './services/projeto.service';
import { NotificationService } from './services/notification.service';
import { environment } from '@environments/environment';

/**
 * Componente principal da aplicação
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatSidenavModule,
    MatListModule,
    MatCardModule,
    MatTabsModule,
    MatSelectModule,
    MatFormFieldModule,
    TarefaFormComponent,
    TarefaListComponent
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = environment.appName;
  version = environment.version;
  
  // Controle de abas
  selectedTabIndex = 0;
  
  // Controle de edição
  tarefaEditando?: TarefaResponse;
  
  // Projetos
  projetos: ProjetoResponse[] = [];
  projetoSelecionado?: number;
  
  // Formulário de seleção de projeto
  projetoForm!: FormGroup;
  
  constructor(
    private fb: FormBuilder,
    private projetoService: ProjetoService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.initProjetoForm();
    this.carregarProjetos();
    this.mostrarBoasVindas();
  }
  
  /**
   * Inicializa formulário de seleção de projeto
   */
  private initProjetoForm(): void {
    this.projetoForm = this.fb.group({
      projetoId: ['']
    });
    
    this.projetoForm.get('projetoId')?.valueChanges.subscribe(projetoId => {
      this.projetoSelecionado = projetoId || undefined;
    });
  }
  
  /**
   * Carrega lista de projetos
   */
  private carregarProjetos(): void {
    this.projetoService.listarTodos().subscribe({
      next: (response) => {
        this.projetos = response.content;
      },
      error: (error) => {
        console.error('Erro ao carregar projetos:', error);
        this.notificationService.error('Erro ao carregar projetos');
      }
    });
  }
  
  /**
   * Mostra mensagem de boas-vindas
   */
  private mostrarBoasVindas(): void {
    setTimeout(() => {
      this.notificationService.info(
        'Bem-vindo ao Sistema de Gerenciamento de Projetos e Tarefas!',
        'Bem-vindo'
      );
    }, 1000);
  }
  
  /**
   * Manipula seleção de aba
   */
  onTabChange(index: number): void {
    this.selectedTabIndex = index;
    
    // Limpa edição ao trocar de aba
    if (index === 1) {
      this.tarefaEditando = undefined;
    }
  }
  
  /**
   * Manipula tarefa salva
   */
  onTarefaSalva(tarefa: TarefaResponse): void {
    // Vai para a aba de listagem
    this.selectedTabIndex = 1;
    this.tarefaEditando = undefined;
  }
  
  /**
   * Manipula cancelamento do formulário
   */
  onCancelarForm(): void {
    this.tarefaEditando = undefined;
    this.selectedTabIndex = 1;
  }
  
  /**
   * Manipula edição de tarefa
   */
  onEditarTarefa(tarefa: TarefaResponse): void {
    this.tarefaEditando = tarefa;
    this.selectedTabIndex = 0; // Vai para a aba do formulário
    
    // Define o projeto da tarefa como selecionado
    this.projetoForm.patchValue({ projetoId: tarefa.idProjeto });
  }
  
  /**
   * Manipula exclusão de tarefa
   */
  onTarefaExcluida(tarefaId: number): void {
    // Se estava editando a tarefa excluída, limpa a edição
    if (this.tarefaEditando?.id === tarefaId) {
      this.tarefaEditando = undefined;
    }
  }
  
  /**
   * Nova tarefa
   */
  onNovaTarefa(): void {
    this.tarefaEditando = undefined;
    this.selectedTabIndex = 0;
  }
  
  /**
   * Obtém nome do projeto selecionado
   */
  getNomeProjetoSelecionado(): string {
    if (!this.projetoSelecionado) return 'Todos os projetos';
    
    const projeto = this.projetos.find(p => p.id === this.projetoSelecionado);
    return projeto ? projeto.nome : 'Projeto não encontrado';
  }
}
