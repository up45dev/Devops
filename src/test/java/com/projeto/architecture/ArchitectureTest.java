package com.projeto.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.*;

/**
 * Testes de arquitetura usando ArchUnit
 * Verifica conformidade com princípios DDD e SOLID
 * Garante que a arquitetura está sendo respeitada
 */
@DisplayName("Testes de Arquitetura")
class ArchitectureTest {
    
    private JavaClasses classes;
    
    @BeforeEach
    void setUp() {
        classes = new ClassFileImporter().importPackages("com.projeto");
    }
    
    @Test
    @DisplayName("Deve respeitar arquitetura em camadas")
    void deveRespeitarArquiteturaEmCamadas() {
        ArchRule layeredArchitectureRule = layeredArchitecture()
            .consideringAllDependencies()
            
            .layer("Presentation").definedBy("..presentation..")
            .layer("Application").definedBy("..application..")
            .layer("Domain").definedBy("..domain..")
            .layer("Infrastructure").definedBy("..infrastructure..")
            
            .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Presentation")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
            .whereLayer("Infrastructure").mayNotAccessAnyLayer();
            
        layeredArchitectureRule.check(classes);
    }
    
    @Test
    @DisplayName("Domínio não deve depender de outras camadas")
    void dominioNaoDeveDependerDeOutrasCamadas() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..application..", "..infrastructure..", "..presentation..");
            
        rule.check(classes);
    }
    
    @Test
    @DisplayName("Entidades devem estar no pacote domain")
    void entidadesDevemEstarNoPacoteDomain() {
        ArchRule rule = classes()
            .that().areAnnotatedWith(jakarta.persistence.Entity.class)
            .should().resideInAPackage("..domain..");
            
        rule.check(classes);
    }
    
    @Test
    @DisplayName("Value Objects devem implementar ValueObject interface")
    void valueObjectsDevemImplementarInterface() {
        ArchRule rule = classes()
            .that().resideInAPackage("..valueobjects..")
            .should().implement(com.projeto.domain.shared.ValueObject.class);
            
        rule.check(classes);
    }
    
    @Test
    @DisplayName("Services de aplicação devem estar anotados com @Service")
    void servicesAplicacaoDevemEstarAnotados() {
        ArchRule rule = classes()
            .that().resideInAPackage("..application..")
            .and().haveSimpleNameEndingWith("ApplicationService")
            .should().beAnnotatedWith(org.springframework.stereotype.Service.class);
            
        rule.check(classes);
    }
    
    @Test
    @DisplayName("Repositories devem ser interfaces")
    void repositoriesDevemSerInterfaces() {
        ArchRule rule = classes()
            .that().haveSimpleNameEndingWith("Repository")
            .and().resideInAPackage("..domain..")
            .should().beInterfaces();
            
        rule.check(classes);
    }
    
    @Test
    @DisplayName("Commands devem ser records imutáveis")
    void commandsDevemSerRecords() {
        ArchRule rule = classes()
            .that().resideInAPackage("..commands..")
            .should().beRecords();
            
        rule.check(classes);
    }
    
    @Test
    @DisplayName("Events devem ser sealed interfaces ou records")
    void eventsDevemSerSealedOuRecords() {
        ArchRule rule = classes()
            .that().resideInAPackage("..events..")
            .should().beInterfaces()
            .orShould().beRecords();
            
        rule.check(classes);
    }
    
    @Test
    @DisplayName("Não deve haver dependências cíclicas entre pacotes")
    void naoDeveHaverDependenciasCiclicas() {
        slices()
            .matching("com.projeto.(*)..")
            .should()
            .beFreeOfCycles()
            .check(classes);
    }
    
    @Test
    @DisplayName("Classes de teste devem ter sufixo Test")
    void classesTesteDevemTerSufixoTest() {
        ArchRule rule = classes()
            .that().resideInAPackage("..test..")
            .should().haveSimpleNameEndingWith("Test");
            
        rule.check(classes);
    }
    
    @Test
    @DisplayName("Controllers devem estar anotados com @RestController")
    void controllersDevemEstarAnotados() {
        ArchRule rule = classes()
            .that().haveSimpleNameEndingWith("Controller")
            .should().beAnnotatedWith(org.springframework.web.bind.annotation.RestController.class);
            
        rule.check(classes);
    }
    
    @Test
    @DisplayName("Entidades de domínio devem estender Entity")
    void entidadesDominioDevemEstenderEntity() {
        ArchRule rule = classes()
            .that().resideInAPackage("..domain..")
            .and().haveSimpleNameNotContaining("Test")
            .and().areNotInterfaces()
            .and().areNotEnums()
            .and().areNotRecords()
            .and().doNotHaveSimpleName("Entity")
            .and().doNotHaveSimpleName("ValueObject")
            .and().doNotHaveSimpleName("AuditInfo")
            .and().doNotResideInAPackage("..valueobjects..")
            .and().doNotResideInAPackage("..events..")
            .and().doNotResideInAPackage("..shared..")
            .should().beAssignableTo(com.projeto.domain.shared.Entity.class);
            
        rule.check(classes);
    }
    
    @Test
    @DisplayName("Application Services devem ser transacionais")
    void applicationServicesDevemSerTransacionais() {
        ArchRule rule = classes()
            .that().haveSimpleNameEndingWith("ApplicationService")
            .should().beAnnotatedWith(org.springframework.transaction.annotation.Transactional.class);
            
        rule.check(classes);
    }
    
    @Test
    @DisplayName("Infrastructure não deve ser acessada diretamente pelo domain")
    void infrastructureNaoDeveSerAcessadaDiretamentePeloDomain() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAPackage("..infrastructure..");
            
        rule.check(classes);
    }
}