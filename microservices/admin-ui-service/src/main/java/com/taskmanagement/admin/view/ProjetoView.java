package com.taskmanagement.admin.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * View para gerenciamento de projetos
 */
@PageTitle("Projetos")
@Route(value = "projetos", layout = MainLayout.class)
public class ProjetoView extends VerticalLayout {
    
    public ProjetoView() {
        H2 header = new H2("📋 Gerenciamento de Projetos");
        add(header);
        
        add(new Paragraph("🚀 Interface para gerenciar todos os projetos do sistema."));
        add(new Paragraph("🚧 Em desenvolvimento: Grid com projetos e funcionalidades CRUD"));
        
        setSizeFull();
    }
}