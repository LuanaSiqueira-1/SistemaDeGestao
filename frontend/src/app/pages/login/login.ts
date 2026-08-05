import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectorRef,
  Component,
} from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';

import {
  Auth,
  LoginRequest,
} from '../../core/services/auth';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  email = '';
  password = '';

  mensagemSucesso = '';
  mensagemErro = '';
  enviando = false;

  constructor(
    private readonly authService: Auth,
    private readonly changeDetector: ChangeDetectorRef,
  ) {}

  entrar(formulario: NgForm): void {
    this.mensagemSucesso = '';
    this.mensagemErro = '';

    if (formulario.invalid) {
      formulario.control.markAllAsTouched();
      this.mensagemErro = 'Preencha o e-mail e a senha corretamente.';
      return;
    }

    const dados: LoginRequest = {
      email: this.email.trim(),
      password: this.password,
    };

    this.enviando = true;

    this.authService.login(dados).subscribe({
      next: (response) => {
        this.enviando = false;

        localStorage.setItem('token', response.token);
        localStorage.setItem('role', response.role);
        localStorage.setItem('nome', response.nome);
        localStorage.setItem('email', response.email);

        this.mensagemSucesso =
          `Login realizado com sucesso. Bem-vindo, ${response.nome}!`;

        formulario.resetForm({
          email: '',
          password: '',
        });

        this.email = '';
        this.password = '';

        this.changeDetector.detectChanges();
      },

      error: (error: HttpErrorResponse) => {
        this.enviando = false;

        if (error.status === 401 || error.status === 403) {
          this.mensagemErro = 'E-mail ou senha inválidos.';
        } else if (error.status === 0) {
          this.mensagemErro = 'Não foi possível conectar ao servidor.';
        } else {
          this.mensagemErro = 'Não foi possível realizar o login.';
        }

        this.changeDetector.detectChanges();
      },
    });
  }
}