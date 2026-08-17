import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectorRef,
  Component,
  OnInit,
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Navbar } from '../../components/navbar/navbar';
import { ClienteDetalheResponse } from '../../core/models/cliente';
import { ClienteService } from '../../core/services/cliente';

@Component({
  selector: 'app-cliente-detalhe',
  imports: [CommonModule, Navbar],
  templateUrl: './cliente-detalhe.html',
  styleUrl: './cliente-detalhe.css',
})
export class ClienteDetalhe implements OnInit {
  cliente: ClienteDetalheResponse | null = null;

  carregando = false;
  mensagemErro = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly clienteService: ClienteService,
    private readonly changeDetector: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isInteger(id) || id <= 0) {
      this.mensagemErro = 'Cliente inválido.';
      return;
    }

    this.carregarCliente(id);
  }

  private carregarCliente(id: number): void {
    this.carregando = true;
    this.mensagemErro = '';

    this.clienteService.buscarPorId(id).subscribe({
      next: (response) => {
        this.cliente = response;
        this.carregando = false;

        this.changeDetector.detectChanges();
      },

      error: (error: HttpErrorResponse) => {
        this.cliente = null;
        this.carregando = false;

        if (error.status === 404) {
          this.mensagemErro = 'Cliente não encontrado.';
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
}
