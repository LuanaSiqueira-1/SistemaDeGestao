import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectorRef,
  Component,
  OnInit,
} from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { Navbar } from '../../components/navbar/navbar';
import {
  StatusVeiculo,
  VeiculoAtualizacaoRequest,
} from '../../core/models/veiculo';
import { VeiculoService } from '../../core/services/veiculo';

interface ErroApi {
  mensagem?: string;
  campos?: Record<string, string>;
}

@Component({
  selector: 'app-veiculo-edicao',
  imports: [
    CommonModule,
    FormsModule,
    Navbar,
  ],
  templateUrl: './veiculo-edicao.html',
  styleUrl: './veiculo-edicao.css',
})
export class VeiculoEdicao implements OnInit {
  veiculoId: number | null = null;

  marca = '';
  modelo = '';
  ano: number | null = null;
  cor = '';
  quilometragem: number | null = null;
  preco: number | null = null;
  status: StatusVeiculo = 'DISPONIVEL';

  veiculoCarregado = false;
  veiculoVendido = false;

  carregando = false;
  enviando = false;

  mensagemErro = '';
  mensagemSucesso = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly veiculoService: VeiculoService,
    private readonly changeDetector: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isInteger(id) || id <= 0) {
      this.mensagemErro = 'Veículo inválido.';
      return;
    }

    this.veiculoId = id;
    this.carregarVeiculo(id);
  }

  salvar(formulario: NgForm): void {
    this.mensagemErro = '';
    this.mensagemSucesso = '';

    if (
      formulario.invalid ||
      this.veiculoId === null ||
      this.ano === null ||
      this.preco === null
    ) {
      formulario.control.markAllAsTouched();

      this.mensagemErro =
        'Preencha os campos obrigatórios corretamente.';

      return;
    }

    const dados: VeiculoAtualizacaoRequest = {
      marca: this.marca.trim(),
      modelo: this.modelo.trim(),
      ano: this.ano,
      cor: this.cor.trim() || undefined,
      quilometragem: this.quilometragem ?? undefined,
      preco: this.preco,
      status: this.status,
    };

    this.enviando = true;

    this.veiculoService.atualizar(
      this.veiculoId,
      dados,
    ).subscribe({
      next: (response) => {
        this.enviando = false;
        this.mensagemErro = '';
        this.mensagemSucesso =
          'Veículo atualizado com sucesso.';

        this.status = response.status;
        this.veiculoVendido =
          response.status === 'VENDIDO';

        this.changeDetector.detectChanges();
      },

      error: (error: HttpErrorResponse) => {
        this.enviando = false;

        if (error.status === 400) {
          this.mensagemErro = this.obterMensagemErro(
            error,
            'Verifique os dados informados.',
          );
        } else if (error.status === 401) {
          this.mensagemErro =
            'Sua sessão expirou. Faça login novamente.';
        } else if (error.status === 403) {
          this.mensagemErro =
            'Você não possui permissão para editar este veículo.';
        } else if (error.status === 404) {
          this.mensagemErro =
            'Veículo não encontrado.';
        } else if (error.status === 409) {
          this.mensagemErro = this.obterMensagemErro(
            error,
            'Não foi possível atualizar o veículo devido a um conflito.',
          );
        } else if (error.status === 0) {
          this.mensagemErro =
            'Não foi possível conectar ao servidor.';
        } else {
          this.mensagemErro =
            'Não foi possível atualizar o veículo.';
        }

        this.changeDetector.detectChanges();
      },
    });
  }

  voltar(): void {
    void this.router.navigate(['/veiculos']);
  }

  private carregarVeiculo(id: number): void {
    this.carregando = true;
    this.veiculoCarregado = false;
    this.mensagemErro = '';
    this.mensagemSucesso = '';

    this.veiculoService.buscarPorId(id).subscribe({
      next: (response) => {
        this.marca = response.marca;
        this.modelo = response.modelo;
        this.ano = response.ano;
        this.cor = response.cor ?? '';
        this.quilometragem =
          response.quilometragem ?? null;
        this.preco = response.preco;
        this.status = response.status;

        this.veiculoVendido =
          response.status === 'VENDIDO';

        this.veiculoCarregado = true;
        this.carregando = false;

        this.changeDetector.detectChanges();
      },

      error: (error: HttpErrorResponse) => {
        this.carregando = false;
        this.veiculoCarregado = false;

        if (error.status === 404) {
          this.mensagemErro =
            'Veículo não encontrado.';
        } else if (error.status === 401) {
          this.mensagemErro =
            'Sua sessão expirou. Faça login novamente.';
        } else if (error.status === 403) {
          this.mensagemErro =
            'Você não possui permissão para consultar este veículo.';
        } else if (error.status === 0) {
          this.mensagemErro =
            'Não foi possível conectar ao servidor.';
        } else {
          this.mensagemErro =
            'Não foi possível carregar os dados do veículo.';
        }

        this.changeDetector.detectChanges();
      },
    });
  }

  private obterMensagemErro(
    error: HttpErrorResponse,
    mensagemPadrao: string,
  ): string {
    const resposta =
      error.error as ErroApi | string | null;

    if (!resposta) {
      return mensagemPadrao;
    }

    if (typeof resposta === 'string') {
      return resposta;
    }

    if (resposta.campos) {
      const mensagens = Object.values(
        resposta.campos,
      )
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

    return mensagemPadrao;
  }
}