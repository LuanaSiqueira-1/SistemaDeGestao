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
  RelatorioVendasFiltros,
  RelatorioVendasResponse,
} from '../../core/models/relatorio-vendas';

import {
  RelatorioVendasService,
} from '../../core/services/relatorio-vendas';

@Component({
  selector: 'app-relatorio-vendas',
  imports: [
    CommonModule,
    FormsModule,
    Navbar,
  ],
  templateUrl: './relatorio-vendas.html',
  styleUrl: './relatorio-vendas.css',
})
export class RelatorioVendas implements OnInit {
  readonly anoAtual = new Date().getFullYear();

  ano = this.anoAtual;
  semestreSelecionado = '';
  marca = '';

  relatorio: RelatorioVendasResponse | null = null;

  carregando = false;
  consultado = false;
  mensagemErro = '';

  constructor(
    private readonly relatorioVendasService:
      RelatorioVendasService,
    private readonly changeDetector:
      ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.consultar();
  }

  consultar(): void {
    this.mensagemErro = '';

    if (
      !Number.isInteger(this.ano) ||
      this.ano < 1000 ||
      this.ano > 9999
    ) {
      this.relatorio = null;
      this.consultado = true;
      this.mensagemErro =
        'Informe um ano válido com quatro dígitos.';
      return;
    }

    const filtros: RelatorioVendasFiltros = {
      ano: this.ano,
    };

    if (
      this.semestreSelecionado === '1' ||
      this.semestreSelecionado === '2'
    ) {
      filtros.semestre =
        Number(this.semestreSelecionado) as 1 | 2;
    }

    const marcaNormalizada = this.marca.trim();

    if (marcaNormalizada) {
      filtros.marca = marcaNormalizada;
    }

    this.carregando = true;
    this.consultado = false;
    this.relatorio = null;

    this.relatorioVendasService
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
    this.ano = this.anoAtual;
    this.semestreSelecionado = '';
    this.marca = '';

    this.consultar();
  }

  get semVendas(): boolean {
    return (
      this.relatorio !== null &&
      this.relatorio.quantidadeVendas === 0
    );
  }

  get possuiVendas(): boolean {
    return (
      this.relatorio !== null &&
      this.relatorio.quantidadeVendas > 0
    );
  }

  periodoDoRelatorio(): string {
    if (!this.relatorio) {
      return '';
    }

    if (this.relatorio.semestre === 1) {
      return `1º semestre de ${this.relatorio.ano}`;
    }

    if (this.relatorio.semestre === 2) {
      return `2º semestre de ${this.relatorio.ano}`;
    }

    return `Ano de ${this.relatorio.ano}`;
  }

  private obterMensagemErro(
    error: HttpErrorResponse,
  ): string {
    if (error.status === 400) {
      return 'Os filtros informados são inválidos. Verifique o ano e o semestre.';
    }

    if (error.status === 401) {
      return 'Sua sessão expirou ou não é válida. Faça login novamente.';
    }

    if (error.status === 403) {
      return 'Você não possui permissão para consultar o relatório de vendas.';
    }

    if (error.status === 0) {
      return 'Não foi possível conectar ao servidor. Verifique se o backend está em execução.';
    }

    return 'Não foi possível carregar o relatório de vendas.';
  }
}