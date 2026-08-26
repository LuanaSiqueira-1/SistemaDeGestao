import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectorRef,
  Component,
  OnInit,
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Navbar } from '../../components/navbar/navbar';
import {
  ClienteDetalheResponse,
  DataVendaHistorico,
  HistoricoCompraResponse,
} from '../../core/models/cliente';
import { ClienteService } from '../../core/services/cliente';

@Component({
  selector: 'app-cliente-detalhe',
  imports: [CommonModule, Navbar],
  templateUrl: './cliente-detalhe.html',
  styleUrl: './cliente-detalhe.css',
})
export class ClienteDetalhe implements OnInit {
  cliente: ClienteDetalheResponse | null = null;
  historicoCompras: HistoricoCompraResponse[] = [];

  carregando = false;
  carregandoHistorico = false;

  mensagemErro = '';
  mensagemErroHistorico = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly clienteService: ClienteService,
    private readonly changeDetector: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    const id = Number(
      this.route.snapshot.paramMap.get('id'),
    );

    if (!Number.isInteger(id) || id <= 0) {
      this.mensagemErro = 'Cliente inválido.';
      return;
    }

    this.carregarCliente(id);
  }

  formatarDataVenda(
    data: DataVendaHistorico,
  ): string {
    if (Array.isArray(data)) {
      const [ano, mes, dia] = data;

      return `${this.comDoisDigitos(dia)}/${this.comDoisDigitos(mes)}/${ano}`;
    }

    const partes = data.split('-');

    if (partes.length === 3) {
      const [ano, mes, dia] = partes;

      return `${dia}/${mes}/${ano}`;
    }

    return data;
  }

  private carregarCliente(id: number): void {
    this.carregando = true;
    this.mensagemErro = '';

    this.clienteService.buscarPorId(id).subscribe({
      next: (response) => {
        this.cliente = response;
        this.carregando = false;

        this.carregarHistorico(id);

        this.changeDetector.detectChanges();
      },

      error: (error: HttpErrorResponse) => {
        this.cliente = null;
        this.historicoCompras = [];
        this.carregando = false;

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

  private carregarHistorico(id: number): void {
    this.carregandoHistorico = true;
    this.mensagemErroHistorico = '';
    this.historicoCompras = [];

    this.clienteService
      .buscarHistoricoCompras(id)
      .subscribe({
        next: (response) => {
          this.historicoCompras = response;
          this.carregandoHistorico = false;

          this.changeDetector.detectChanges();
        },

        error: (error: HttpErrorResponse) => {
          this.historicoCompras = [];
          this.carregandoHistorico = false;

          if (error.status === 404) {
            this.mensagemErroHistorico =
              'Cliente não encontrado.';
          } else if (error.status === 401) {
            this.mensagemErroHistorico =
              'Sua sessão expirou. Faça login novamente.';
          } else if (error.status === 403) {
            this.mensagemErroHistorico =
              'Você não possui permissão para consultar o histórico de compras.';
          } else if (error.status === 0) {
            this.mensagemErroHistorico =
              'Não foi possível conectar ao servidor.';
          } else {
            this.mensagemErroHistorico =
              'Não foi possível carregar o histórico de compras.';
          }

          this.changeDetector.detectChanges();
        },
      });
  }

  private comDoisDigitos(valor: number): string {
    return valor.toString().padStart(2, '0');
  }
}