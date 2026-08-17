import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectorRef,
  Component,
  OnInit,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Navbar } from '../../components/navbar/navbar';
import { ClienteListagemResponse } from '../../core/models/cliente';
import { ClienteService } from '../../core/services/cliente';

@Component({
  selector: 'app-cliente-listagem',
  imports: [CommonModule, FormsModule, RouterLink, Navbar],
  templateUrl: './cliente-listagem.html',
  styleUrl: './cliente-listagem.css',
})
export class ClienteListagem implements OnInit {
  clientes: ClienteListagemResponse[] = [];

  nome = '';
  cpf = '';

  paginaAtual = 0;
  totalPaginas = 0;
  totalElementos = 0;
  readonly tamanhoPagina = 10;

  carregando = false;
  mensagemErro = '';

  constructor(
    private readonly clienteService: ClienteService,
    private readonly changeDetector: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.carregarClientes();
  }

  pesquisar(): void {
    this.paginaAtual = 0;
    this.carregarClientes();
  }

  limparPesquisa(): void {
    this.nome = '';
    this.cpf = '';
    this.paginaAtual = 0;

    this.carregarClientes();
  }

  paginaAnterior(): void {
    if (this.paginaAtual === 0 || this.carregando) {
      return;
    }

    this.paginaAtual--;
    this.carregarClientes();
  }

  proximaPagina(): void {
    if (
      this.paginaAtual >= this.totalPaginas - 1 ||
      this.carregando
    ) {
      return;
    }

    this.paginaAtual++;
    this.carregarClientes();
  }

  private carregarClientes(): void {
    this.carregando = true;
    this.mensagemErro = '';

    this.clienteService
      .pesquisar(
        this.nome,
        this.cpf,
        this.paginaAtual,
        this.tamanhoPagina,
      )
      .subscribe({
        next: (response) => {
          this.clientes = response.content;
          this.totalPaginas = response.totalPages;
          this.totalElementos = response.totalElements;
          this.paginaAtual = response.number;
          this.carregando = false;

          this.changeDetector.detectChanges();
        },

        error: (error: HttpErrorResponse) => {
          this.clientes = [];
          this.totalPaginas = 0;
          this.totalElementos = 0;
          this.carregando = false;

          if (error.status === 401) {
            this.mensagemErro =
              'Sua sessão expirou. Faça login novamente.';
          } else if (error.status === 403) {
            this.mensagemErro =
              'Você não possui permissão para consultar clientes.';
          } else if (error.status === 0) {
            this.mensagemErro =
              'Não foi possível conectar ao servidor.';
          } else {
            this.mensagemErro =
              'Não foi possível carregar os clientes.';
          }

          this.changeDetector.detectChanges();
        },
      });
  }
}
