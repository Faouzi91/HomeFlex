import {
  HttpClient,
  HttpInterceptorFn,
  provideHttpClient,
  withInterceptors,
} from '@angular/common/http';
import {
  ApplicationConfig,
  importProvidersFrom,
  provideBrowserGlobalErrorListeners,
} from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { provideRouter, withInMemoryScrolling } from '@angular/router';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader, provideTranslateHttpLoader } from '@ngx-translate/http-loader';

import { routes } from './app.routes';

function readCookie(name: string): string | null {
  const item = document.cookie.split('; ').find((cookie) => cookie.startsWith(`${name}=`));

  return item ? decodeURIComponent(item.split('=').slice(1).join('=')) : null;
}

const credentialsInterceptor: HttpInterceptorFn = (req, next) => {
  const mutating = !['GET', 'HEAD', 'OPTIONS'].includes(req.method);
  let headers = req.headers;
  const token = readCookie('XSRF-TOKEN');

  if (mutating && token && !headers.has('X-XSRF-TOKEN')) {
    headers = headers.set('X-XSRF-TOKEN', token);
  }

  return next(
    req.clone({
      withCredentials: true,
      headers,
    }),
  );
};

export const appConfig: ApplicationConfig = {
  providers: [
    CurrencyPipe,
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes, withInMemoryScrolling({ scrollPositionRestoration: 'enabled' })),
    provideHttpClient(withInterceptors([credentialsInterceptor])),
    provideTranslateHttpLoader({
      prefix: './assets/i18n/',
      suffix: '.json',
    }),
    importProvidersFrom(
      TranslateModule.forRoot({
        defaultLanguage: 'en',
        loader: {
          provide: TranslateLoader,
          useClass: TranslateHttpLoader,
        },
      }),
    ),
  ],
};
