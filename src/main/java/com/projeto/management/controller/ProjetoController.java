package com.projeto.management.controller;

import com.projeto.management.dto.request.ProjetoRequestDTO;
import com.projeto.management.dto.response.ProjetoResponseDTO;
import com.projeto.management.model.enums.StatusProjeto;
import com.projeto.management.service.ProjetoService;
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
 * Controller para gerenciamento de Projetos
 */
@RestController
@RequestMapping("/api/projetos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Projetos", description = "API para gerenciamento de projetos")
public class ProjetoController {
    
    private final ProjetoService projetoService;
    
    @Operation(summary = "Criar novo projeto", description = "Cria um novo projeto no sistema")
    @PostMapping
    public ResponseEntity<ProjetoResponseDTO> criarProjeto(
            @Valid @RequestBody ProjetoRequestDTO requestDTO,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        
        log.info("POST /api/projetos - Criando projeto: {}", requestDTO.getNome());
        
        ProjetoResponseDTO response = projetoService.criarProjeto(requestDTO, usuario);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(summary = "Buscar projeto por ID", description = "Retorna um projeto específico pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProjetoResponseDTO> buscarPorId(
            @Parameter(description = "ID do projeto") @PathVariable Long id) {
        
        log.info("GET /api/projetos/{} - Buscando projeto", id);
        
        ProjetoResponseDTO response = projetoService.buscarPorId(id);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Listar projetos", description = "Lista projetos com paginação e filtros opcionais")
    @GetMapping
    public ResponseEntity<Page<ProjetoResponseDTO>> listarProjetos(
            @Parameter(description = "Filtro por nome do projeto") @RequestParam(required = false) String nome,
            @Parameter(description = "Filtro por status") @RequestParam(required = false) StatusProjeto status,
            @Parameter(description = "Filtro por responsável") @RequestParam(required = false) String responsavel,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("GET /api/projetos - Listando projetos com filtros");
        
        Page<ProjetoResponseDTO> response = projetoService.listarProjetos(nome, status, responsavel, pageable);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Atualizar projeto", description = "Atualiza um projeto existente")
    @PutMapping("/{id}")
    public ResponseEntity<ProjetoResponseDTO> atualizarProjeto(
            @Parameter(description = "ID do projeto") @PathVariable Long id,
            @Valid @RequestBody ProjetoRequestDTO requestDTO,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        
        log.info("PUT /api/projetos/{} - Atualizando projeto", id);
        
        ProjetoResponseDTO response = projetoService.atualizarProjeto(id, requestDTO, usuario);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Excluir projeto", description = "Exclui logicamente um projeto")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirProjeto(
            @Parameter(description = "ID do projeto") @PathVariable Long id,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        
        log.info("DELETE /api/projetos/{} - Excluindo projeto", id);
        
        projetoService.excluirProjeto(id, usuario);
        
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Alterar status do projeto", description = "Altera o status de um projeto")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProjetoResponseDTO> alterarStatus(
            @Parameter(description = "ID do projeto") @PathVariable Long id,
            @Parameter(description = "Novo status") @RequestParam StatusProjeto status,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        
        log.info("PATCH /api/projetos/{}/status - Alterando status para: {}", id, status);
        
        ProjetoResponseDTO response = projetoService.alterarStatus(id, status, usuario);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Buscar projetos atrasados", description = "Retorna lista de projetos com data fim prevista vencida")
    @GetMapping("/atrasados")
    public ResponseEntity<List<ProjetoResponseDTO>> buscarProjetosAtrasados() {
        
        log.info("GET /api/projetos/atrasados - Buscando projetos atrasados");
        
        List<ProjetoResponseDTO> response = projetoService.buscarProjetosAtrasados();
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Buscar projetos por responsável", description = "Retorna projetos de um responsável específico")
    @GetMapping("/responsavel/{responsavel}")
    public ResponseEntity<List<ProjetoResponseDTO>> buscarPorResponsavel(
            @Parameter(description = "Nome do responsável") @PathVariable String responsavel) {
        
        log.info("GET /api/projetos/responsavel/{} - Buscando projetos", responsavel);
        
        List<ProjetoResponseDTO> response = projetoService.buscarPorResponsavel(responsavel);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Estatísticas dos projetos", description = "Retorna estatísticas gerais dos projetos")
    @GetMapping("/estatisticas")
    public ResponseEntity<Object> getEstatisticas() {
        
        log.info("GET /api/projetos/estatisticas - Gerando estatísticas");
        
        Object response = projetoService.getEstatisticasProjetos();
        
        return ResponseEntity.ok(response);
    }
}
