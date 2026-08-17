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
import { SessaoService } from '../../core/services/sessao';

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
  usuarioLogado = false;

  constructor(
    private readonly authService: Auth,
    private readonly sessaoService: SessaoService,
    private readonly changeDetector: ChangeDetectorRef,
    private readonly router: Router,
  ) {
    this.usuarioLogado =
      this.sessaoService.possuiSessaoValida();
  }

  entrar(formulario: NgForm): void {
    this.mensagemSucesso = '';
    this.mensagemErro = '';

    if (formulario.invalid) {
      formulario.control.markAllAsTouched();
      this.mensagemErro =
        'Preencha o e-mail e a senha corretamente.';
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
        this.sessaoService.salvarSessao(
          response.token,
          response.role,
          response.nome,
          response.email,
        );

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

        if (
          error.status === 401 ||
          error.status === 403
        ) {
          this.mensagemErro =
            'E-mail ou senha inválidos.';
        } else if (error.status === 0) {
          this.mensagemErro =
            'Não foi possível conectar ao servidor.';
        } else {
          this.mensagemErro =
            'Não foi possível realizar o login.';
        }

        this.atualizarTela();
      },
    });
  }

  sair(): void {
    this.sessaoService.limparSessao();

    this.enviando = false;
    this.usuarioLogado = false;
    this.email = '';
    this.password = '';
    this.mensagemErro = '';
    this.mensagemSucesso =
      'Sessão encerrada com sucesso.';

    this.atualizarTela();

    this.router.navigate(['/login']);
  }

  private atualizarTela(): void {
    this.changeDetector.detectChanges();
  }
}