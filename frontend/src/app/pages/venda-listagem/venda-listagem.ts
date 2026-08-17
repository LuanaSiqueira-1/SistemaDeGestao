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
import { VendaListagemResponse } from '../../core/models/venda';
import { VendaService } from '../../core/services/venda';

@Component({
  selector: 'app-venda-listagem',
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    Navbar,
  ],
  templateUrl: './venda-listagem.html',
  styleUrl: './venda-listagem.css',
})
export class VendaListagem implements OnInit {
  vendas: VendaListagemResponse[] = [];

  cliente = '';
  veiculo = '';

  paginaAtual = 0;
  totalPaginas = 0;
  totalElementos = 0;

  readonly tamanhoPagina = 10;

  carregando = false;
  mensagemErro = '';

  constructor(
    private readonly vendaService: VendaService,
    private readonly changeDetector: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.carregarVendas();
  }

  pesquisar(): void {
    this.paginaAtual = 0;
    this.carregarVendas();
  }

  limparPesquisa(): void {
    this.cliente = '';
    this.veiculo = '';
    this.paginaAtual = 0;

    this.carregarVendas();
  }

  paginaAnterior(): void {
    if (this.paginaAtual === 0 || this.carregando) {
      return;
    }

    this.paginaAtual--;
    this.carregarVendas();
  }

  proximaPagina(): void {
    if (
      this.paginaAtual >= this.totalPaginas - 1 ||
      this.carregando
    ) {
      return;
    }

    this.paginaAtual++;
    this.carregarVendas();
  }

  private carregarVendas(): void {
    this.carregando = true;
    this.mensagemErro = '';

    this.vendaService
      .pesquisar(
        this.cliente,
        this.veiculo,
        this.paginaAtual,
        this.tamanhoPagina,
      )
      .subscribe({
        next: (response) => {
          this.vendas = response.content;
          this.totalPaginas = response.totalPages;
          this.totalElementos = response.totalElements;
          this.paginaAtual = response.number;
          this.carregando = false;

          this.changeDetector.detectChanges();
        },

        error: (error: HttpErrorResponse) => {
          this.vendas = [];
          this.totalPaginas = 0;
          this.totalElementos = 0;
          this.carregando = false;

          if (error.status === 401) {
            this.mensagemErro =
              'Sua sessão expirou. Faça login novamente.';
          } else if (error.status === 403) {
            this.mensagemErro =
              'Você não possui permissão para consultar vendas.';
          } else if (error.status === 0) {
            this.mensagemErro =
              'Não foi possível conectar ao servidor.';
          } else {
            this.mensagemErro =
              'Não foi possível carregar as vendas.';
          }

          this.changeDetector.detectChanges();
        },
      });
  }
}