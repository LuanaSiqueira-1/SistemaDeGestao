import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { finalize } from 'rxjs';

import {
  Auth,
  RegisterRequest,
  Role,
} from '../../core/services/auth';

@Component({
  selector: 'app-register',
  imports: [CommonModule, FormsModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  nome = '';
  email = '';
  senha = '';
  role: Role = 'USER';

  mensagemSucesso = '';
  mensagemErro = '';
  enviando = false;

  constructor(private readonly authService: Auth) {}

  cadastrar(formulario: NgForm): void {
    this.mensagemSucesso = '';
    this.mensagemErro = '';

    if (formulario.invalid) {
      formulario.control.markAllAsTouched();
      this.mensagemErro = 'Preencha todos os campos corretamente.';
      return;
    }

    const dados: RegisterRequest = {
      nome: this.nome.trim(),
      email: this.email.trim(),
      senha: this.senha,
      role: this.role,
    };

    this.enviando = true;

    this.authService
      .register(dados)
      .pipe(
        finalize(() => {
          this.enviando = false;
        }),
      )
      .subscribe({
        next: (response) => {
          this.mensagemSucesso =
            `Usuário ${response.nome} cadastrado com sucesso.`;

          formulario.resetForm({
            nome: '',
            email: '',
            senha: '',
            role: 'USER',
          });

          this.nome = '';
          this.email = '';
          this.senha = '';
          this.role = 'USER';
        },

        error: (error: HttpErrorResponse) => {
          this.mensagemErro = this.obterMensagemErro(error);
        },
      });
  }

  private obterMensagemErro(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'Não foi possível conectar ao servidor.';
    }

    if (error.status === 409) {
      return error.error?.erro ?? 'Este e-mail já está cadastrado.';
    }

    if (error.status === 400 && error.error) {
      if (typeof error.error === 'string') {
        return error.error;
      }

      const mensagens = Object.values(error.error)
        .filter((mensagem) => typeof mensagem === 'string')
        .join(' ');

      return mensagens || 'Verifique os dados informados.';
    }

    return 'Não foi possível realizar o cadastro.';
  }
}