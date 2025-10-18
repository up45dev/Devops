package com.taskmanagement.projeto.controller;

import com.taskmanagement.projeto.entity.Projeto;
import com.taskmanagement.projeto.entity.StatusProjeto;
import com.taskmanagement.projeto.service.ProjetoService;
import com.taskmanagement.projeto.dto.ProjetoRequest;
import com.taskmanagement.projeto.dto.ProjetoResponse;
import com.taskmanagement.projeto.dto.ApiResponse;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/**
 * Controller REST para Projetos com HATEOAS
 * Implementa APIs maduras com hypermedia
 */
@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {
    
    @Autowired
    private ProjetoService projetoService;
    
    /**
     * Lista todos os projetos com paginação e HATEOAS
     */
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Projeto>>> getAllProjetos(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String responsavel) {
        
        Page<Projeto> projetos;
        
        if (status != null) {
            projetos = projetoService.findByStatus(StatusProjeto.valueOf(status.toUpperCase()), pageable);
        } else if (responsavel != null) {
            projetos = projetoService.findByResponsavel(responsavel, pageable);
        } else {
            projetos = projetoService.findAll(pageable);
        }
        
        PagedModel<EntityModel<Projeto>> pagedModel = createPagedModel(projetos);
        
        return ResponseEntity.ok(pagedModel);
    }
    
    /**
     * Busca projeto por ID com HATEOAS
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Projeto>> getProjetoById(@PathVariable Long id) {
        
        Projeto projeto = projetoService.findById(id);
        EntityModel<Projeto> projetoModel = createEntityModel(projeto);
        
        return ResponseEntity.ok(projetoModel);
    }
    
    /**
     * Cria novo projeto
     */
    @PostMapping
    public ResponseEntity<EntityModel<Projeto>> createProjeto(
            @Valid @RequestBody ProjetoRequest request,
            @RequestHeader("X-User-Name") String username) {
        
        Projeto projeto = projetoService.createProjeto(request, username);
        EntityModel<Projeto> projetoModel = createEntityModel(projeto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(projetoModel);
    }
    
    /**
     * Atualiza projeto existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Projeto>> updateProjeto(
            @PathVariable Long id,
            @Valid @RequestBody ProjetoRequest request,
            @RequestHeader("X-User-Name") String username) {
        
        Projeto projeto = projetoService.updateProjeto(id, request, username);
        EntityModel<Projeto> projetoModel = createEntityModel(projeto);
        
        return ResponseEntity.ok(projetoModel);
    }
    
    /**
     * Exclui projeto
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProjeto(
            @PathVariable Long id,
            @RequestHeader("X-User-Name") String username) {
        
        projetoService.deleteProjeto(id, username);
        
        return ResponseEntity.ok(new ApiResponse(true, "Projeto excluído com sucesso"));
    }
    
    /**
     * Inicia projeto
     */
    @PostMapping("/{id}/iniciar")
    public ResponseEntity<EntityModel<Projeto>> iniciarProjeto(
            @PathVariable Long id,
            @RequestHeader("X-User-Name") String username) {
        
        Projeto projeto = projetoService.iniciarProjeto(id, username);
        EntityModel<Projeto> projetoModel = createEntityModel(projeto);
        
        return ResponseEntity.ok(projetoModel);
    }
    
    /**
     * Finaliza projeto
     */
    @PostMapping("/{id}/finalizar")
    public ResponseEntity<EntityModel<Projeto>> finalizarProjeto(
            @PathVariable Long id,
            @RequestHeader("X-User-Name") String username) {
        
        Projeto projeto = projetoService.finalizarProjeto(id, username);
        EntityModel<Projeto> projetoModel = createEntityModel(projeto);
        
        return ResponseEntity.ok(projetoModel);
    }
    
    /**
     * Cancela projeto
     */
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<EntityModel<Projeto>> cancelarProjeto(
            @PathVariable Long id,
            @RequestHeader("X-User-Name") String username) {
        
        Projeto projeto = projetoService.cancelarProjeto(id, username);
        EntityModel<Projeto> projetoModel = createEntityModel(projeto);
        
        return ResponseEntity.ok(projetoModel);
    }
    
    /**
     * Busca projetos por termo
     */
    @GetMapping("/search")
    public ResponseEntity<CollectionModel<EntityModel<Projeto>>> searchProjetos(
            @RequestParam String q) {
        
        List<Projeto> projetos = projetoService.searchProjetos(q);
        CollectionModel<EntityModel<Projeto>> collectionModel = createCollectionModel(projetos);
        
        return ResponseEntity.ok(collectionModel);
    }
    
    /**
     * Estatísticas de projetos
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getProjetoStats() {
        return ResponseEntity.ok(projetoService.getProjetoStats());
    }
    
    /**
     * Cria EntityModel com links HATEOAS para um projeto
     */
    private EntityModel<Projeto> createEntityModel(Projeto projeto) {
        EntityModel<Projeto> projetoModel = EntityModel.of(projeto);
        
        // Self link
        projetoModel.add(linkTo(methodOn(ProjetoController.class).getProjetoById(projeto.getId())).withSelfRel());
        
        // Collection link
        projetoModel.add(linkTo(ProjetoController.class).withRel("projetos"));
        
        // Action links based on status
        if (projeto.getStatus() == StatusProjeto.PLANEJAMENTO) {
            projetoModel.add(linkTo(methodOn(ProjetoController.class).iniciarProjeto(projeto.getId(), null)).withRel("iniciar"));
        }
        
        if (projeto.getStatus() == StatusProjeto.EM_ANDAMENTO) {
            projetoModel.add(linkTo(methodOn(ProjetoController.class).finalizarProjeto(projeto.getId(), null)).withRel("finalizar"));
        }
        
        if (projeto.getStatus() != StatusProjeto.CONCLUIDO && projeto.getStatus() != StatusProjeto.CANCELADO) {
            projetoModel.add(linkTo(methodOn(ProjetoController.class).cancelarProjeto(projeto.getId(), null)).withRel("cancelar"));
            projetoModel.add(linkTo(methodOn(ProjetoController.class).updateProjeto(projeto.getId(), null, null)).withRel("update"));
            projetoModel.add(linkTo(methodOn(ProjetoController.class).deleteProjeto(projeto.getId(), null)).withRel("delete"));
        }
        
        return projetoModel;
    }
    
    /**
     * Cria CollectionModel com links HATEOAS
     */
    private CollectionModel<EntityModel<Projeto>> createCollectionModel(List<Projeto> projetos) {
        List<EntityModel<Projeto>> projetoModels = projetos.stream()
                .map(this::createEntityModel)
                .toList();
        
        CollectionModel<EntityModel<Projeto>> collectionModel = CollectionModel.of(projetoModels);
        collectionModel.add(linkTo(ProjetoController.class).withSelfRel());
        
        return collectionModel;
    }
    
    /**
     * Cria PagedModel com links HATEOAS para paginação
     */
    private PagedModel<EntityModel<Projeto>> createPagedModel(Page<Projeto> page) {
        List<EntityModel<Projeto>> projetoModels = page.getContent().stream()
                .map(this::createEntityModel)
                .toList();
        
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages()
        );
        
        PagedModel<EntityModel<Projeto>> pagedModel = PagedModel.of(projetoModels, metadata);
        pagedModel.add(linkTo(ProjetoController.class).withSelfRel());
        
        return pagedModel;
    }
}