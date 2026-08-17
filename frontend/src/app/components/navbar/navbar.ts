import { Component } from '@angular/core';
import {
  Router,
  RouterLink,
  RouterLinkActive,
} from '@angular/router';

import { SessaoService } from '../../core/services/sessao';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  constructor(
    private readonly sessaoService: SessaoService,
    private readonly router: Router,
  ) {}

  logout(): void {
    this.sessaoService.limparSessao();

    this.router.navigate(['/login']);
  }
}