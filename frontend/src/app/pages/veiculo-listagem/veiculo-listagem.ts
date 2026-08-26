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
import { VeiculoListagemResponse } from '../../core/models/veiculo';
import { VeiculoService } from '../../core/services/veiculo';

@Component({
  selector: 'app-veiculo-listagem',
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    Navbar,
  ],
  templateUrl: './veiculo-listagem.html',
  styleUrl: './veiculo-listagem.css',
})
export class VeiculoListagem implements OnInit {
  veiculos: VeiculoListagemResponse[] = [];
  veiculosFiltrados: VeiculoListagemResponse[] = [];

  termoPesquisa = '';
  statusSelecionado = '';

  carregando = false;
  mensagemErro = '';

  constructor(
    private readonly veiculoService: VeiculoService,
    private readonly changeDetector: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.listarVeiculos();
  }

  listarVeiculos(): void {
    this.carregando = true;
    this.mensagemErro = '';

    this.veiculoService.listar().subscribe({
      next: (response) => {
        this.veiculos = response;
        this.veiculosFiltrados = response;

        this.carregando = false;

        this.changeDetector.detectChanges();
      },

      error: (error: HttpErrorResponse) => {
        this.carregando = false;

        if (error.status === 401) {
          this.mensagemErro =
            'Sua sessão expirou. Faça login novamente.';
        } else if (error.status === 403) {
          this.mensagemErro =
            'Você não possui permissão para consultar veículos.';
        } else if (error.status === 0) {
          this.mensagemErro =
            'Não foi possível conectar ao servidor.';
        } else {
          this.mensagemErro =
            'Não foi possível carregar os veículos.';
        }

        this.changeDetector.detectChanges();
      },
    });
  }

  pesquisar(): void {
    const termo = this.normalizarTexto(this.termoPesquisa);

    this.veiculosFiltrados = this.veiculos.filter(
      (veiculo) => {
        const marca =
          this.normalizarTexto(veiculo.marca);
        const modelo =
          this.normalizarTexto(veiculo.modelo);

        const correspondeTermo =
          !termo ||
          marca.includes(termo) ||
          modelo.includes(termo);

        const correspondeStatus =
          !this.statusSelecionado ||
          veiculo.status === this.statusSelecionado;

        return correspondeTermo && correspondeStatus;
      },
    );
  }

  limparPesquisa(): void {
    this.termoPesquisa = '';
    this.statusSelecionado = '';
    this.veiculosFiltrados = [...this.veiculos];
  }

  formatarStatus(status: string): string {
    if (status === 'DISPONIVEL') {
      return 'Disponível';
    }

    if (status === 'VENDIDO') {
      return 'Vendido';
    }

    if (status === 'EM_MANUTENCAO') {
      return 'Em manutenção';
    }

    return status;
  }

  private normalizarTexto(valor: string): string {
    return valor
      .trim()
      .toLocaleLowerCase('pt-BR')
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '');
  }
}