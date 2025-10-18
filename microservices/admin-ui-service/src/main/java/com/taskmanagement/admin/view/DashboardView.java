package com.taskmanagement.admin.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Dashboard principal da aplicação administrativa
 */
@PageTitle("Dashboard")
@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {
    
    public DashboardView() {
        setSpacing(false);
        
        H2 header = new H2("📊 Dashboard Administrativo");
        add(header);
        
        add(new Paragraph("📈 Bem-vindo ao painel administrativo do Task Management System!"));
        add(new Paragraph("🛠️ Aqui você pode gerenciar projetos, usuários e visualizar relatórios."));
        
        // TODO: Adicionar widgets de estatísticas
        add(new Paragraph("🚧 Em desenvolvimento: Widgets de estatísticas em tempo real"));
        
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }
}