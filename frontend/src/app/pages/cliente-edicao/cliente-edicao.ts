import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectorRef,
  Component,
  OnInit,
} from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { Navbar } from '../../components/navbar/navbar';
import {
  ClienteAtualizacaoRequest,
} from '../../core/models/cliente';
import { ClienteService } from '../../core/services/cliente';

interface ErroApi {
  mensagem?: string;
  campos?: Record<string, string>;
}

@Component({
  selector: 'app-cliente-edicao',
  imports: [
    CommonModule,
    FormsModule,
    Navbar,
  ],
  templateUrl: './cliente-edicao.html',
  styleUrl: './cliente-edicao.css',
})
export class ClienteEdicao implements OnInit {
  clienteId: number | null = null;

  nome = '';
  cpf = '';
  telefone = '';
  email = '';

  clienteCarregado = false;
  carregando = false;
  enviando = false;

  mensagemErro = '';
  mensagemSucesso = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly clienteService: ClienteService,
    private readonly changeDetector: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isInteger(id) || id <= 0) {
      this.mensagemErro = 'Cliente inválido.';
      return;
    }

    this.clienteId = id;
    this.carregarCliente(id);
  }

  salvar(formulario: NgForm): void {
    this.mensagemErro = '';
    this.mensagemSucesso = '';

    if (
      formulario.invalid ||
      this.clienteId === null
    ) {
      formulario.control.markAllAsTouched();

      this.mensagemErro =
        'Preencha os campos obrigatórios corretamente.';

      return;
    }

    const dados: ClienteAtualizacaoRequest = {
      nome: this.nome.trim(),
      cpf: this.cpf.trim(),
      telefone: this.telefone.trim(),
      email: this.email.trim(),
    };

    this.enviando = true;

    this.clienteService
      .atualizar(this.clienteId, dados)
      .subscribe({
        next: (response) => {
          this.nome = response.nome;
          this.cpf = response.cpf;
          this.telefone = response.telefone;
          this.email = response.email;

          this.enviando = false;
          this.mensagemErro = '';
          this.mensagemSucesso =
            'Cliente atualizado com sucesso.';

          this.changeDetector.detectChanges();
        },

        error: (error: HttpErrorResponse) => {
          this.enviando = false;

          if (error.status === 400) {
            this.mensagemErro =
              this.obterMensagemErro(
                error,
                'Verifique os dados informados.',
              );
          } else if (error.status === 401) {
            this.mensagemErro =
              'Sua sessão expirou. Faça login novamente.';
          } else if (error.status === 403) {
            this.mensagemErro =
              'Você não possui permissão para editar este cliente.';
          } else if (error.status === 404) {
            this.mensagemErro =
              'Cliente não encontrado.';
          } else if (error.status === 409) {
            this.mensagemErro =
              this.obterMensagemErro(
                error,
                'Não foi possível atualizar o cliente devido a um conflito.',
              );
          } else if (error.status === 0) {
            this.mensagemErro =
              'Não foi possível conectar ao servidor.';
          } else {
            this.mensagemErro =
              'Não foi possível atualizar o cliente.';
          }

          this.changeDetector.detectChanges();
        },
      });
  }

  voltar(): void {
    void this.router.navigate(['/clientes']);
  }

  private carregarCliente(id: number): void {
    this.carregando = true;
    this.clienteCarregado = false;
    this.mensagemErro = '';
    this.mensagemSucesso = '';

    this.clienteService.buscarPorId(id).subscribe({
      next: (response) => {
        this.nome = response.nome;
        this.cpf = response.cpf;
        this.telefone = response.telefone;
        this.email = response.email;

        this.clienteCarregado = true;
        this.carregando = false;

        this.changeDetector.detectChanges();
      },

      error: (error: HttpErrorResponse) => {
        this.carregando = false;
        this.clienteCarregado = false;

        if (error.status === 404) {
          this.mensagemErro =
            'Cliente não encontrado.';
        } else if (error.status === 401) {
          this.mensagemErro =
            'Sua sessão expirou. Faça login novamente.';
        } else if (error.status === 403) {
          this.mensagemErro =
            'Você não possui permissão para consultar este cliente.';
        } else if (error.status === 0) {
          this.mensagemErro =
            'Não foi possível conectar ao servidor.';
        } else {
          this.mensagemErro =
            'Não foi possível carregar os dados do cliente.';
        }

        this.changeDetector.detectChanges();
      },
    });
  }

  private obterMensagemErro(
    error: HttpErrorResponse,
    mensagemPadrao: string,
  ): string {
    const resposta =
      error.error as ErroApi | string | null;

    if (!resposta) {
      return mensagemPadrao;
    }

    if (typeof resposta === 'string') {
      return resposta;
    }

    if (resposta.campos) {
      const mensagens = Object.values(
        resposta.campos,
      )
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