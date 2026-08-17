import {
  HttpErrorResponse,
  HttpInterceptorFn,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import {
  catchError,
  throwError,
} from 'rxjs';

import { SessaoService } from '../services/sessao';

export const authInterceptor: HttpInterceptorFn = (
  request,
  next,
) => {
  const router = inject(Router);
  const sessaoService = inject(SessaoService);

  const requisicaoDeAutenticacao =
    request.url.includes('/api/auth/');

  const token = sessaoService.obterTokenValido();

  const requisicao =
    token && !requisicaoDeAutenticacao
      ? request.clone({
          setHeaders: {
            Authorization: `Bearer ${token}`,
          },
        })
      : request;

  return next(requisicao).pipe(
    catchError((error: HttpErrorResponse) => {
      if (
        error.status === 401 &&
        !requisicaoDeAutenticacao
      ) {
        sessaoService.limparSessao();

        void router.navigate(['/login']);
      }

      return throwError(() => error);
    }),
  );
};