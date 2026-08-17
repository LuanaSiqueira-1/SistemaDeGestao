import { inject } from '@angular/core';
import {
  CanActivateFn,
  Router,
} from '@angular/router';

import { SessaoService } from '../services/sessao';

export const roleGuard: CanActivateFn = (route) => {
  const router = inject(Router);
  const sessaoService = inject(SessaoService);

  if (!sessaoService.possuiSessaoValida()) {
    return router.createUrlTree(['/login']);
  }

  const role = sessaoService.obterRole();

  const rolesPermitidas =
    route.data['roles'] as string[] | undefined;

  if (
    rolesPermitidas?.includes(role ?? '')
  ) {
    return true;
  }

  return router.createUrlTree(['/dashboard']);
};