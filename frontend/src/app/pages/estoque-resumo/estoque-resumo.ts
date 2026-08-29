import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectorRef,
  Component,
  OnInit,
} from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Navbar } from '../../components/navbar/navbar';

import {
  EstoqueFiltros,
  EstoqueResumoResponse,
} from '../../core/models/estoque';

import {
  EstoqueService,
} from '../../core/services/estoque';

@Component({
  selector: 'app-estoque-resumo',
  imports: [
    CommonModule,
    FormsModule,
    Navbar,
  ],
  templateUrl: './estoque-resumo.html',
  styleUrl: './estoque-resumo.css',
})
export class EstoqueResumo implements OnInit {
  marca = '';
  statusSelecionado = '';

  relatorio: EstoqueResumoResponse | null = null;

  carregando = false;
  consultado = false;
  mensagemErro = '';

  constructor(
    private readonly estoqueService: EstoqueService,
    private readonly changeDetector: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.consultar();
  }

  consultar(): void {
    const filtros: EstoqueFiltros = {};

    const marcaNormalizada = this.marca.trim();

    if (marcaNormalizada) {
      filtros.marca = marcaNormalizada;
    }

    if (this.statusSelecionado) {
      filtros.status = this.statusSelecionado;
    }

    this.carregando = true;
    this.consultado = false;
    this.mensagemErro = '';
    this.relatorio = null;

    this.estoqueService
      .consultar(filtros)
      .subscribe({
        next: (response) => {
          this.relatorio = response;
          this.carregando = false;
          this.consultado = true;

          this.changeDetector.detectChanges();
        },

        error: (error: HttpErrorResponse) => {
          this.relatorio = null;
          this.carregando = false;
          this.consultado = true;
          this.mensagemErro =
            this.obterMensagemErro(error);

          this.changeDetector.detectChanges();
        },
      });
  }

  limparFiltros(): void {
    this.marca = '';
    this.statusSelecionado = '';

    this.consultar();
  }

  get estoqueVazio(): boolean {
    return (
      this.relatorio !== null &&
      this.relatorio.quantidadeTotal === 0
    );
  }

  get possuiVeiculos(): boolean {
    return (
      this.relatorio !== null &&
      this.relatorio.quantidadeTotal > 0
    );
  }

  formatarFaixaPreco(faixa: string): string {
    switch (faixa) {
      case 'ATE_50000':
        return 'Até R$ 50 mil';

      case 'DE_50000_A_100000':
        return 'R$ 50 mil a R$ 100 mil';

      case 'DE_100000_A_150000':
        return 'R$ 100 mil a R$ 150 mil';

      case 'ACIMA_150000':
        return 'Acima de R$ 150 mil';

      default:
        return faixa;
    }
  }

  private obterMensagemErro(
    error: HttpErrorResponse,
  ): string {
    if (error.status === 400) {
      return 'Os filtros informados são inválidos. Verifique o status selecionado.';
    }

    if (error.status === 401) {
      return 'Sua sessão expirou ou não é válida. Faça login novamente.';
    }

    if (error.status === 403) {
      return 'Você não possui permissão para consultar o acompanhamento do estoque.';
    }

    if (error.status === 0) {
      return 'Não foi possível conectar ao servidor. Verifique se o backend está em execução.';
    }

    return 'Não foi possível carregar o acompanhamento do estoque.';
  }
}