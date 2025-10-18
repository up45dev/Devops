package com.projeto.management.controller;

import com.projeto.management.dto.request.TarefaRequestDTO;
import com.projeto.management.dto.response.TarefaResponseDTO;
import com.projeto.management.model.enums.StatusTarefa;
import com.projeto.management.model.enums.Prioridade;
import com.projeto.management.service.TarefaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciamento de Tarefas
 */
@RestController
@RequestMapping("/api/tarefas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tarefas", description = "API para gerenciamento de tarefas")
public class TarefaController {
    
    private final TarefaService tarefaService;
    
    @Operation(summary = "Criar nova tarefa", description = "Cria uma nova tarefa no sistema")
    @PostMapping
    public ResponseEntity<TarefaResponseDTO> criarTarefa(
            @Valid @RequestBody TarefaRequestDTO requestDTO,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        
        log.info("POST /api/tarefas - Criando tarefa: {}", requestDTO.getTitulo());
        
        TarefaResponseDTO response = tarefaService.criarTarefa(requestDTO, usuario);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(summary = "Buscar tarefa por ID", description = "Retorna uma tarefa específica pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<TarefaResponseDTO> buscarPorId(
            @Parameter(description = "ID da tarefa") @PathVariable Long id) {
        
        log.info("GET /api/tarefas/{} - Buscando tarefa", id);
        
        TarefaResponseDTO response = tarefaService.buscarPorId(id);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Listar tarefas", description = "Lista tarefas com paginação e filtros opcionais")
    @GetMapping
    public ResponseEntity<Page<TarefaResponseDTO>> listarTarefas(
            @Parameter(description = "Filtro por projeto") @RequestParam(required = false) Long projetoId,
            @Parameter(description = "Filtro por título") @RequestParam(required = false) String titulo,
            @Parameter(description = "Filtro por status") @RequestParam(required = false) StatusTarefa status,
            @Parameter(description = "Filtro por responsável") @RequestParam(required = false) String responsavel,
            @Parameter(description = "Filtro por prioridade") @RequestParam(required = false) Prioridade prioridade,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("GET /api/tarefas - Listando tarefas com filtros");
        
        Page<TarefaResponseDTO> response = tarefaService.listarTarefas(
            projetoId, titulo, status, responsavel, prioridade, pageable);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Listar tarefas por projeto", description = "Lista todas as tarefas de um projeto específico")
    @GetMapping("/projeto/{projetoId}")
    public ResponseEntity<List<TarefaResponseDTO>> listarTarefasPorProjeto(
            @Parameter(description = "ID do projeto") @PathVariable Long projetoId) {
        
        log.info("GET /api/tarefas/projeto/{} - Listando tarefas", projetoId);
        
        List<TarefaResponseDTO> response = tarefaService.listarTarefasPorProjeto(projetoId);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Atualizar tarefa", description = "Atualiza uma tarefa existente")
    @PutMapping("/{id}")
    public ResponseEntity<TarefaResponseDTO> atualizarTarefa(
            @Parameter(description = "ID da tarefa") @PathVariable Long id,
            @Valid @RequestBody TarefaRequestDTO requestDTO,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        
        log.info("PUT /api/tarefas/{} - Atualizando tarefa", id);
        
        TarefaResponseDTO response = tarefaService.atualizarTarefa(id, requestDTO, usuario);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Excluir tarefa", description = "Exclui logicamente uma tarefa")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirTarefa(
            @Parameter(description = "ID da tarefa") @PathVariable Long id,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        
        log.info("DELETE /api/tarefas/{} - Excluindo tarefa", id);
        
        tarefaService.excluirTarefa(id, usuario);
        
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Alterar status da tarefa", description = "Altera o status de uma tarefa")
    @PatchMapping("/{id}/status")
    public ResponseEntity<TarefaResponseDTO> alterarStatus(
            @Parameter(description = "ID da tarefa") @PathVariable Long id,
            @Parameter(description = "Novo status") @RequestParam StatusTarefa status,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        
        log.info("PATCH /api/tarefas/{}/status - Alterando status para: {}", id, status);
        
        TarefaResponseDTO response = tarefaService.alterarStatus(id, status, usuario);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Atualizar percentual de conclusão", description = "Atualiza o percentual de conclusão de uma tarefa")
    @PatchMapping("/{id}/percentual")
    public ResponseEntity<TarefaResponseDTO> atualizarPercentual(
            @Parameter(description = "ID da tarefa") @PathVariable Long id,
            @Parameter(description = "Percentual de conclusão (0-100)") @RequestParam Integer percentual,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        
        log.info("PATCH /api/tarefas/{}/percentual - Atualizando para: {}%", id, percentual);
        
        TarefaResponseDTO response = tarefaService.atualizarPercentual(id, percentual, usuario);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Buscar tarefas atrasadas", description = "Retorna lista de tarefas com data fim prevista vencida")
    @GetMapping("/atrasadas")
    public ResponseEntity<List<TarefaResponseDTO>> buscarTarefasAtrasadas() {
        
        log.info("GET /api/tarefas/atrasadas - Buscando tarefas atrasadas");
        
        List<TarefaResponseDTO> response = tarefaService.buscarTarefasAtrasadas();
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Buscar tarefas que vencem em X dias", description = "Retorna tarefas que vencem no período especificado")
    @GetMapping("/vencendo-em/{dias}")
    public ResponseEntity<List<TarefaResponseDTO>> buscarTarefasVencendoEm(
            @Parameter(description = "Número de dias") @PathVariable int dias) {
        
        log.info("GET /api/tarefas/vencendo-em/{} - Buscando tarefas", dias);
        
        List<TarefaResponseDTO> response = tarefaService.buscarTarefasVencendoEm(dias);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Buscar tarefas de alta prioridade", description = "Retorna tarefas de alta prioridade em aberto")
    @GetMapping("/alta-prioridade")
    public ResponseEntity<List<TarefaResponseDTO>> buscarTarefasAltaPrioridade() {
        
        log.info("GET /api/tarefas/alta-prioridade - Buscando tarefas");
        
        List<TarefaResponseDTO> response = tarefaService.buscarTarefasAltaPrioridade();
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Buscar tarefas por responsável", description = "Retorna tarefas de um responsável específico")
    @GetMapping("/responsavel/{responsavel}")
    public ResponseEntity<List<TarefaResponseDTO>> buscarPorResponsavel(
            @Parameter(description = "Nome do responsável") @PathVariable String responsavel) {
        
        log.info("GET /api/tarefas/responsavel/{} - Buscando tarefas", responsavel);
        
        List<TarefaResponseDTO> response = tarefaService.buscarPorResponsavel(responsavel);
        
        return ResponseEntity.ok(response);
    }
}
