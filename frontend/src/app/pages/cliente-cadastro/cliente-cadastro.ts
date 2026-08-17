import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectorRef,
  Component,
} from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';

import { Navbar } from '../../components/navbar/navbar';
import { ClienteCadastroRequest } from '../../core/models/cliente';
import { ClienteService } from '../../core/services/cliente';

interface ErroApi {
  mensagem?: string;
  campos?: Record<string, string>;
}

@Component({
  selector: 'app-cliente-cadastro',
  imports: [CommonModule, FormsModule, Navbar],
  templateUrl: './cliente-cadastro.html',
  styleUrl: './cliente-cadastro.css',
})
export class ClienteCadastro {
  nome = '';
  cpf = '';
  telefone = '';
  email = '';

  mensagemSucesso = '';
  mensagemErro = '';
  enviando = false;

  constructor(
    private readonly clienteService: ClienteService,
    private readonly changeDetector: ChangeDetectorRef,
  ) {}

  cadastrar(formulario: NgForm): void {
    this.mensagemSucesso = '';
    this.mensagemErro = '';

    if (formulario.invalid) {
      formulario.control.markAllAsTouched();

      this.mensagemErro =
        'Preencha os campos obrigatórios corretamente.';

      return;
    }

    const dados: ClienteCadastroRequest = {
      nome: this.nome.trim(),
      cpf: this.cpf.trim(),
      telefone: this.telefone.trim(),
      email: this.email.trim(),
    };

    this.enviando = true;

    this.clienteService.cadastrar(dados).subscribe({
      next: () => {
        this.enviando = false;
        this.mensagemErro = '';
        this.mensagemSucesso =
          'Cliente cadastrado com sucesso.';

        formulario.resetForm({
          nome: '',
          cpf: '',
          telefone: '',
          email: '',
        });

        this.nome = '';
        this.cpf = '';
        this.telefone = '';
        this.email = '';

        this.changeDetector.detectChanges();
      },

      error: (error: HttpErrorResponse) => {
        this.enviando = false;

        if (error.status === 400) {
          this.mensagemErro =
            this.obterMensagemErro(error);
        } else if (error.status === 401) {
          this.mensagemErro =
            'Sua sessão expirou. Faça login novamente.';
        } else if (error.status === 403) {
          this.mensagemErro =
            'Você não possui permissão para cadastrar clientes.';
        } else if (error.status === 409) {
          this.mensagemErro =
            this.obterMensagemErro(
              error,
              'Não foi possível cadastrar o cliente devido a um conflito.',
            );
        } else if (error.status === 0) {
          this.mensagemErro =
            'Não foi possível conectar ao servidor.';
        } else {
          this.mensagemErro =
            'Não foi possível cadastrar o cliente.';
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