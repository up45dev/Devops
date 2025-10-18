import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';

/**
 * Inicialização da aplicação Angular
 */
bootstrapApplication(AppComponent, appConfig)
  .catch(err => console.error('Erro ao inicializar a aplicação:', err));
