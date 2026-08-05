import { inject } from '@angular/core';
import {
  CanActivateFn,
  Router,
} from '@angular/router';

export const roleGuard: CanActivateFn = (route) => {
  const router = inject(Router);

  const token = localStorage.getItem('token');
  const role = localStorage.getItem('role');

  const rolesPermitidas = route.data['roles'] as string[];

  if (!token) {
    return router.createUrlTree(['/login']);
  }

  if (rolesPermitidas.includes(role ?? '')) {
    return true;
  }

  return router.createUrlTree(['/dashboard']);
};