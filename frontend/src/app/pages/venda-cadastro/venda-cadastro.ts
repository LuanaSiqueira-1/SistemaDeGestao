import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectorRef,
  Component,
  OnInit,
} from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';

import { Navbar } from '../../components/navbar/navbar';
import { ClienteListagemResponse } from '../../core/models/cliente';
import { VeiculoListagemResponse } from '../../core/models/veiculo';
import { VendaRequestDTO } from '../../core/models/venda';
import { ClienteService } from '../../core/services/cliente';
import { VeiculoService } from '../../core/services/veiculo';
import { VendaService } from '../../core/services/venda';

interface ErroApi {
  mensagem?: string;
  campos?: Record<string, string>;
}

@Component({
  selector: 'app-venda-cadastro',
  imports: [CommonModule, FormsModule, Navbar],
  templateUrl: './venda-cadastro.html',
  styleUrl: './venda-cadastro.css',
})
export class VendaCadastro implements OnInit {
  clientes: ClienteListagemResponse[] = [];
  veiculosDisponiveis: VeiculoListagemResponse[] = [];

  clienteId: number | null = null;
  veiculoId: number | null = null;
  dataVenda = '';
  valor: number | null = null;

  carregandoClientes = false;
  carregandoVeiculos = false;
  enviando = false;

  mensagemSucesso = '';
  mensagemErro = '';

  constructor(
    private readonly clienteService: ClienteService,
    private readonly veiculoService: VeiculoService,
    private readonly vendaService: VendaService,
    private readonly changeDetector: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.carregarClientes();
    this.carregarVeiculos();
  }

  get carregandoDados(): boolean {
    return this.carregandoClientes || this.carregandoVeiculos;
  }

  registrar(formulario: NgForm): void {
    this.mensagemSucesso = '';
    this.mensagemErro = '';

    if (
      formulario.invalid ||
      this.clienteId === null ||
      this.veiculoId === null ||
      this.valor === null
    ) {
      formulario.control.markAllAsTouched();
      this.mensagemErro =
        'Preencha os campos obrigatórios corretamente.';
      return;
    }

    const dados: VendaRequestDTO = {
      dataVenda: this.dataVenda,
      valor: this.valor,
      veiculoId: this.veiculoId,
      clienteId: this.clienteId,
    };

    const veiculoVendidoId = this.veiculoId;

    this.enviando = true;

    this.vendaService.registrar(dados).subscribe({
      next: () => {
        this.enviando = false;
        this.mensagemErro = '';
        this.mensagemSucesso =
          'Venda registrada com sucesso.';

        this.veiculosDisponiveis =
          this.veiculosDisponiveis.filter(
            (veiculo) => veiculo.id !== veiculoVendidoId,
          );

        formulario.resetForm({
          clienteId: null,
          veiculoId: null,
          dataVenda: '',
          valor: null,
        });

        this.clienteId = null;
        this.veiculoId = null;
        this.dataVenda = '';
        this.valor = null;

        this.changeDetector.detectChanges();
      },

      error: (error: HttpErrorResponse) => {
        this.enviando = false;

        if (error.status === 400) {
          this.mensagemErro = this.obterMensagemErro(error);
        } else if (error.status === 404) {
          this.mensagemErro =
            'Cliente ou veículo não encontrado.';
        } else if (error.status === 401) {
          this.mensagemErro =
            'Sua sessão expirou. Faça login novamente.';
        } else if (error.status === 403) {
          this.mensagemErro =
            'Você não possui permissão para registrar vendas.';
        } else if (error.status === 0) {
          this.mensagemErro =
            'Não foi possível conectar ao servidor.';
        } else {
          this.mensagemErro =
            'Não foi possível registrar a venda.';
        }

        this.changeDetector.detectChanges();
      },
    });
  }

  private carregarClientes(pagina = 0): void {
    if (pagina === 0) {
      this.clientes = [];
      this.carregandoClientes = true;
    }

    this.clienteService.listar(pagina, 10).subscribe({
      next: (response) => {
        this.clientes = [
          ...this.clientes,
          ...response.content,
        ];

        if (!response.last) {
          this.carregarClientes(pagina + 1);
          return;
        }

        this.carregandoClientes = false;
        this.changeDetector.detectChanges();
      },

      error: () => {
        this.clientes = [];
        this.carregandoClientes = false;
        this.mensagemErro =
          'Não foi possível carregar os clientes.';

        this.changeDetector.detectChanges();
      },
    });
  }

  private carregarVeiculos(): void {
    this.carregandoVeiculos = true;

    this.veiculoService.listar().subscribe({
      next: (response) => {
        this.veiculosDisponiveis = response.filter(
          (veiculo) => veiculo.status === 'DISPONIVEL',
        );

        this.carregandoVeiculos = false;
        this.changeDetector.detectChanges();
      },

      error: () => {
        this.veiculosDisponiveis = [];
        this.carregandoVeiculos = false;
        this.mensagemErro =
          'Não foi possível carregar os veículos.';

        this.changeDetector.detectChanges();
      },
    });
  }

  private obterMensagemErro(error: HttpErrorResponse): string {
    const resposta = error.error as ErroApi | null;

    if (!resposta) {
      return 'Verifique os dados informados.';
    }

    if (resposta.campos) {
      const mensagens = Object.values(resposta.campos)
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

    return 'Verifique os dados informados.';
  }
}