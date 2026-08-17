import { Injectable } from '@angular/core';

import { Role } from './auth';

@Injectable({
  providedIn: 'root',
})
export class SessaoService {
  salvarSessao(
    token: string,
    role: Role,
    nome: string,
    email: string,
  ): void {
    localStorage.setItem('token', token);
    localStorage.setItem('role', role);
    localStorage.setItem('nome', nome);
    localStorage.setItem('email', email);
  }

  limparSessao(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('nome');
    localStorage.removeItem('email');
  }

  obterTokenValido(): string | null {
    const token = localStorage.getItem('token');

    if (!token) {
      return null;
    }

    if (this.tokenExpiradoOuInvalido(token)) {
      this.limparSessao();
      return null;
    }

    return token;
  }

  obterRole(): Role | null {
    const role = localStorage.getItem('role');

    if (role === 'ADMIN' || role === 'USER') {
      return role;
    }

    return null;
  }

  possuiSessaoValida(): boolean {
    return this.obterTokenValido() !== null;
  }

  private tokenExpiradoOuInvalido(token: string): boolean {
    try {
      const partes = token.split('.');

      if (partes.length !== 3) {
        return true;
      }

      const payloadBase64Url = partes[1];

      const payloadBase64 = payloadBase64Url
        .replace(/-/g, '+')
        .replace(/_/g, '/');

      const payloadComPadding = payloadBase64.padEnd(
        Math.ceil(payloadBase64.length / 4) * 4,
        '=',
      );

      const payload = JSON.parse(
        atob(payloadComPadding),
      ) as {
        exp?: number;
      };

      if (typeof payload.exp !== 'number') {
        return true;
      }

      return payload.exp * 1000 <= Date.now();
    } catch {
      return true;
    }
  }
}