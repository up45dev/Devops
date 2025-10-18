package com.taskmanagement.admin.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * View para gerenciamento de usuários
 */
@PageTitle("Usuários")
@Route(value = "usuarios", layout = MainLayout.class)
public class UserView extends VerticalLayout {
    
    public UserView() {
        H2 header = new H2("👥 Gerenciamento de Usuários");
        add(header);
        
        add(new Paragraph("🔐 Interface para gerenciar usuários e permissões."));
        add(new Paragraph("🚧 Em desenvolvimento: Grid com usuários e gestão de roles"));
        
        setSizeFull();
    }
}