import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectorRef,
  Component,
  OnInit,
} from '@angular/core';

import { VeiculoListagemResponse } from '../../core/models/veiculo';
import { VeiculoService } from '../../core/services/veiculo';

@Component({
  selector: 'app-veiculo-listagem',
  imports: [CommonModule],
  templateUrl: './veiculo-listagem.html',
  styleUrl: './veiculo-listagem.css',
})
export class VeiculoListagem implements OnInit {
  veiculos: VeiculoListagemResponse[] = [];

  carregando = false;
  mensagemErro = '';

  constructor(
    private readonly veiculoService: VeiculoService,
    private readonly changeDetector: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.listarVeiculos();
  }

  // Ricardo - consulta os veículos cadastrados ao abrir a tela.
  listarVeiculos(): void {
    this.carregando = true;
    this.mensagemErro = '';

    this.veiculoService.listar().subscribe({
      next: (response) => {
        this.veiculos = response;
        this.carregando = false;

        this.changeDetector.detectChanges();
      },

      error: (error: HttpErrorResponse) => {
        this.carregando = false;

        if (error.status === 401) {
          this.mensagemErro = 'Sua sessão expirou. Faça login novamente.';
        } else if (error.status === 403) {
          this.mensagemErro = 'Você não possui permissão para consultar veículos.';
        } else if (error.status === 0) {
          this.mensagemErro = 'Não foi possível conectar ao servidor.';
        } else {
          this.mensagemErro = 'Não foi possível carregar os veículos.';
        }

        this.changeDetector.detectChanges();
      },
    });
  }
}