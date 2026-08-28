import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth-guard';
import { roleGuard } from './core/guards/role-guard';
import { Admin } from './pages/admin/admin';
import { ClienteCadastro } from './pages/cliente-cadastro/cliente-cadastro';
import { ClienteDetalhe } from './pages/cliente-detalhe/cliente-detalhe';
import { ClienteEdicao } from './pages/cliente-edicao/cliente-edicao';
import { ClienteListagem } from './pages/cliente-listagem/cliente-listagem';
import { Dashboard } from './pages/dashboard/dashboard';
import { EstoqueResumo } from './pages/estoque-resumo/estoque-resumo';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { RelatorioVendas } from './pages/relatorio-vendas/relatorio-vendas';
import { VeiculoCadastro } from './pages/veiculo-cadastro/veiculo-cadastro';
import { VeiculoEdicao } from './pages/veiculo-edicao/veiculo-edicao';
import { VeiculoListagem } from './pages/veiculo-listagem/veiculo-listagem';
import { VendaCadastro } from './pages/venda-cadastro/venda-cadastro';
import { VendaDetalhe } from './pages/venda-detalhe/venda-detalhe';
import { VendaListagem } from './pages/venda-listagem/venda-listagem';

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
    path: 'relatorios/vendas',
    component: RelatorioVendas,
    canActivate: [authGuard],
  },
  {
    path: 'estoque',
    component: EstoqueResumo,
    canActivate: [authGuard],
  },
  {
    path: 'veiculos/cadastro',
    component: VeiculoCadastro,
    canActivate: [authGuard],
  },
  {
    path: 'veiculos/:id/editar',
    component: VeiculoEdicao,
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
    path: 'clientes/:id/editar',
    component: ClienteEdicao,
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
    path: 'vendas/:id',
    component: VendaDetalhe,
    canActivate: [authGuard],
  },
  {
    path: 'vendas',
    component: VendaListagem,
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