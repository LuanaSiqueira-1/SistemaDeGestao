import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectorRef,
  Component,
  OnInit,
} from '@angular/core';
import {
  ActivatedRoute,
  RouterLink,
} from '@angular/router';

import { Navbar } from '../../components/navbar/navbar';
import { StatusVeiculo } from '../../core/models/veiculo';
import { VendaDetalheResponse } from '../../core/models/venda';
import { VendaService } from '../../core/services/venda';

@Component({
  selector: 'app-venda-detalhe',
  imports: [CommonModule, RouterLink, Navbar],
  templateUrl: './venda-detalhe.html',
  styleUrl: './venda-detalhe.css',
})
export class VendaDetalhe implements OnInit {
  venda: VendaDetalheResponse | null = null;

  carregando = false;
  mensagemErro = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly vendaService: VendaService,
    private readonly changeDetector: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isInteger(id) || id <= 0) {
      this.mensagemErro = 'Venda inválida.';
      return;
    }

    this.carregarVenda(id);
  }

  formatarStatusVeiculo(status: StatusVeiculo): string {
    switch (status) {
      case 'DISPONIVEL':
        return 'Disponível';

      case 'VENDIDO':
        return 'Vendido';

      case 'EM_MANUTENCAO':
        return 'Em manutenção';
    }
  }

  private carregarVenda(id: number): void {
    this.carregando = true;
    this.mensagemErro = '';

    this.vendaService.buscarPorId(id).subscribe({
      next: (response) => {
        this.venda = response;
        this.carregando = false;

        this.changeDetector.detectChanges();
      },

      error: (error: HttpErrorResponse) => {
        this.venda = null;
        this.carregando = false;

        if (error.status === 404) {
          this.mensagemErro = 'Venda não encontrada.';
        } else if (error.status === 401) {
          this.mensagemErro =
            'Sua sessão expirou. Faça login novamente.';
        } else if (error.status === 403) {
          this.mensagemErro =
            'Você não possui permissão para consultar esta venda.';
        } else if (error.status === 0) {
          this.mensagemErro =
            'Não foi possível conectar ao servidor.';
        } else {
          this.mensagemErro =
            'Não foi possível carregar os dados da venda.';
        }

        this.changeDetector.detectChanges();
      },
    });
  }
}