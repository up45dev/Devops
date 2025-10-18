// Configurações de ambiente para produção
export const environment = {
  production: true,
  apiUrl: 'https://api.exemplo.com/api',
  appName: 'Sistema de Gerenciamento de Projetos',
  version: '1.0.0',
  
  // Configurações da aplicação
  pagination: {
    defaultPageSize: 20,
    pageSizeOptions: [10, 20, 50, 100]
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
