import { inject } from '@angular/core';
import {
  CanActivateFn,
  Router,
} from '@angular/router';

import { SessaoService } from '../services/sessao';

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);
  const sessaoService = inject(SessaoService);

  if (sessaoService.possuiSessaoValida()) {
    return true;
  }

  return router.createUrlTree(['/login']);
};