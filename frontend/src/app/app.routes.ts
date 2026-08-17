import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth-guard';
import { roleGuard } from './core/guards/role-guard';
import { Admin } from './pages/admin/admin';
import { ClienteCadastro } from './pages/cliente-cadastro/cliente-cadastro';
import { ClienteDetalhe } from './pages/cliente-detalhe/cliente-detalhe';
import { ClienteListagem } from './pages/cliente-listagem/cliente-listagem';
import { Dashboard } from './pages/dashboard/dashboard';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { VeiculoCadastro } from './pages/veiculo-cadastro/veiculo-cadastro';
import { VeiculoListagem } from './pages/veiculo-listagem/veiculo-listagem';
import { VendaCadastro } from './pages/venda-cadastro/venda-cadastro';

export const routes: Routes = [
  {
    path: 'login',
    component: Login,
  },
  {
    path: 'register',
    component: Register,
  },
  {
    path: 'dashboard',
    component: Dashboard,
    canActivate: [authGuard],
  },
  {
    path: 'veiculos/cadastro',
    component: VeiculoCadastro,
    canActivate: [authGuard],
  },
  {
    path: 'veiculos',
    component: VeiculoListagem,
    canActivate: [authGuard],
  },
  {
    path: 'clientes/cadastro',
    component: ClienteCadastro,
    canActivate: [authGuard],
  },
  {
    path: 'clientes',
    component: ClienteListagem,
    canActivate: [authGuard],
  },
  {
    path: 'clientes/:id',
    component: ClienteDetalhe,
    canActivate: [authGuard],
  },
  {
    path: 'vendas/cadastro',
    component: VendaCadastro,
    canActivate: [authGuard],
  },
  {
    path: 'admin',
    component: Admin,
    canActivate: [authGuard, roleGuard],
    data: {
      roles: ['ADMIN'],
    },
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login',
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];