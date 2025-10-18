package com.taskmanagement.admin.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * View para configurações
 */
@PageTitle("Configurações")
@Route(value = "configuracoes", layout = MainLayout.class)
public class SettingsView extends VerticalLayout {
    
    public SettingsView() {
        H2 header = new H2("⚙️ Configurações do Sistema");
        add(header);
        
        add(new Paragraph("🔧 Interface para configurar parâmetros do sistema."));
        add(new Paragraph("🚧 Em desenvolvimento: Forms de configuração"));
        
        setSizeFull();
    }
}