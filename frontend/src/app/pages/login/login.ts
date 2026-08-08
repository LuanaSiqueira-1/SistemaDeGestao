import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectorRef,
  Component,
} from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import {
  Router,
  RouterLink,
} from '@angular/router';

import {
  Auth,
  LoginRequest,
} from '../../core/services/auth';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  email = '';
  password = '';

  mensagemSucesso = '';
  mensagemErro = '';
  enviando = false;
  usuarioLogado = this.possuiSessao();

  constructor(
    private readonly authService: Auth,
    private readonly changeDetector: ChangeDetectorRef,
    private readonly router: Router,
  ) {}

  entrar(formulario: NgForm): void {
    this.mensagemSucesso = '';
    this.mensagemErro = '';

    if (formulario.invalid) {
      formulario.control.markAllAsTouched();
      this.mensagemErro = 'Preencha o e-mail e a senha corretamente.';
      this.atualizarTela();
      return;
    }

    const dados: LoginRequest = {
      email: this.email.trim(),
      password: this.password,
    };

    this.enviando = true;
    this.atualizarTela();

    this.authService.login(dados).subscribe({
      next: (response) => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('role', response.role);
        localStorage.setItem('nome', response.nome);
        localStorage.setItem('email', response.email);

        this.enviando = false;
        this.usuarioLogado = true;
        this.mensagemErro = '';
        this.mensagemSucesso =
          `Login realizado com sucesso. Bem-vindo, ${response.nome}!`;

        formulario.resetForm({
          email: '',
          password: '',
        });

        this.email = '';
        this.password = '';

        this.atualizarTela();

        this.router.navigate(['/dashboard']);
      },

      error: (error: HttpErrorResponse) => {
        this.enviando = false;
        this.usuarioLogado = false;
        this.mensagemSucesso = '';

        if (error.status === 401 || error.status === 403) {
          this.mensagemErro = 'E-mail ou senha inválidos.';
        } else if (error.status === 0) {
          this.mensagemErro = 'Não foi possível conectar ao servidor.';
        } else {
          this.mensagemErro = 'Não foi possível realizar o login.';
        }

        this.atualizarTela();
      },
    });
  }

  sair(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('nome');
    localStorage.removeItem('email');

    this.enviando = false;
    this.usuarioLogado = false;
    this.email = '';
    this.password = '';
    this.mensagemErro = '';
    this.mensagemSucesso = 'Sessão encerrada com sucesso.';

    this.atualizarTela();

    this.router.navigate(['/login']);
  }

  private possuiSessao(): boolean {
    return Boolean(localStorage.getItem('token'));
  }

  private atualizarTela(): void {
    this.changeDetector.detectChanges();
  }
}