// Configurações de ambiente
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  appName: 'Sistema de Gerenciamento de Projetos',
  version: '1.0.0',
  
  // Configurações da aplicação
  pagination: {
    defaultPageSize: 10,
    pageSizeOptions: [5, 10, 25, 50]
  },
  
  // Configurações de notificação
  notification: {
    defaultDuration: 3000,
    errorDuration: 5000
  },
  
  // Headers HTTP padrão
  defaultHeaders: {
    'Content-Type': 'application/json',
    'X-Usuario': 'usuario-frontend'
  }
};
