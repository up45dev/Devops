package com.taskmanagement.admin.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Layout principal da aplicação administrativa
 */
public class MainLayout extends AppLayout {
    
    private H1 viewTitle;
    
    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }
    
    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");
        
        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        
        addToNavbar(true, toggle, viewTitle);
    }
    
    private void addDrawerContent() {
        H1 appName = new H1("Task Management Admin");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);
        
        Scroller scroller = new Scroller(createNavigation());
        
        addToDrawer(header, scroller);
    }
    
    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        
        nav.addItem(new SideNavItem("Dashboard", DashboardView.class, VaadinIcon.DASHBOARD.create()));
        nav.addItem(new SideNavItem("Projetos", ProjetoView.class, VaadinIcon.FOLDER.create()));
        nav.addItem(new SideNavItem("Usuários", UserView.class, VaadinIcon.USERS.create()));
        nav.addItem(new SideNavItem("Relatórios", ReportView.class, VaadinIcon.CHART.create()));
        nav.addItem(new SideNavItem("Configurações", SettingsView.class, VaadinIcon.COG.create()));
        
        return nav;
    }
    
    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }
    
    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}