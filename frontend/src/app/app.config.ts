import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';

import { HttpInterceptorService } from '@shared/index';

/**
 * Configuração da aplicação Angular
 */
export const appConfig: ApplicationConfig = {
  providers: [
    // Roteamento (para futuras expansões)
    provideRouter([]),
    
    // Animações
    provideAnimationsAsync(),
    
    // HTTP Client
    provideHttpClient(),
    
    // Interceptor HTTP
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpInterceptorService,
      multi: true
    },
    
    // Localização para datas
    { provide: MAT_DATE_LOCALE, useValue: 'pt-BR' },
    
    // Módulos do Angular Material
    importProvidersFrom(
      MatSnackBarModule,
      MatDialogModule
    )
  ]
};
