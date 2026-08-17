import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import {
  ChangeDetectorRef,
  Component,
} from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';

import {
  Auth,
  RegisterRequest,
  Role,
} from '../../core/services/auth';

interface ErroApi {
  mensagem?: string;
  campos?: Record<string, string>;
}

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

  constructor(
    private readonly authService: Auth,
    private readonly changeDetector: ChangeDetectorRef,
    private readonly router: Router,
  ) {}

  cadastrar(formulario: NgForm): void {
    this.mensagemSucesso = '';
    this.mensagemErro = '';

    if (formulario.invalid) {
      formulario.control.markAllAsTouched();
      this.mensagemErro =
        'Preencha todos os campos corretamente.';
      return;
    }

    const dados: RegisterRequest = {
      nome: this.nome.trim(),
      email: this.email.trim(),
      senha: this.senha,
      role: this.role,
    };

    this.enviando = true;

    this.authService.register(dados).subscribe({
      next: (response) => {
        this.enviando = false;

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

        this.changeDetector.detectChanges();

        this.router.navigate(['/login']);
      },

      error: (error: HttpErrorResponse) => {
        this.enviando = false;

        if (error.status === 400) {
          this.mensagemErro =
            this.obterMensagemErro(error);
        } else if (error.status === 409) {
          this.mensagemErro =
            this.obterMensagemErro(
              error,
              'E-mail já cadastrado.',
            );
        } else if (error.status === 0) {
          this.mensagemErro =
            'Não foi possível conectar ao servidor.';
        } else {
          this.mensagemErro =
            'Não foi possível realizar o cadastro.';
        }

        this.changeDetector.detectChanges();
      },
    });
  }

  private obterMensagemErro(
    error: HttpErrorResponse,
    mensagemPadrao = 'Verifique os dados informados.',
  ): string {
    const resposta = error.error as ErroApi | string | null;

    if (!resposta) {
      return mensagemPadrao;
    }

    if (typeof resposta === 'string') {
      return resposta;
    }

    if (resposta.campos) {
      const mensagens = Object.values(resposta.campos)
        .filter(
          (mensagem): mensagem is string =>
            typeof mensagem === 'string',
        )
        .join(' ');

      if (mensagens) {
        return mensagens;
      }
    }

    if (
      typeof resposta.mensagem === 'string' &&
      resposta.mensagem.trim()
    ) {
      return resposta.mensagem;
    }

    return mensagemPadrao;
  }
}