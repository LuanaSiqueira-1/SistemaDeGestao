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

        if (error.status === 409) {
          this.mensagemErro =
            error.error?.erro ?? 'E-mail já cadastrado.';
        } else {
          this.mensagemErro = this.obterMensagemErro(error);
        }

        this.changeDetector.detectChanges();
      },
    });
  }

  private obterMensagemErro(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'Não foi possível conectar ao servidor.';
    }

    if (error.status === 400 && error.error) {
      if (typeof error.error === 'string') {
        return error.error;
      }

      const mensagens = Object.values(error.error)
        .filter(
          (mensagem): mensagem is string =>
            typeof mensagem === 'string',
        )
        .join(' ');

      return mensagens || 'Verifique os dados informados.';
    }

    return 'Não foi possível realizar o cadastro.';
  }
}