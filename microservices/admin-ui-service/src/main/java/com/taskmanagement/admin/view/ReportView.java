package com.taskmanagement.admin.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * View para relatórios
 */
@PageTitle("Relatórios")
@Route(value = "relatorios", layout = MainLayout.class)
public class ReportView extends VerticalLayout {
    
    public ReportView() {
        H2 header = new H2("📈 Relatórios e Análises");
        add(header);
        
        add(new Paragraph("📊 Interface para visualizar relatórios e métricas do sistema."));
        add(new Paragraph("🚧 Em desenvolvimento: Gráficos e dashboards interativos"));
        
        setSizeFull();
    }
}