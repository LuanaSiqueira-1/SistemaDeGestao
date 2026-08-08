import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectorRef,
  Component,
} from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';

import {
  StatusVeiculo,
  VeiculoCadastroRequest,
} from '../../core/models/veiculo';
import { VeiculoService } from '../../core/services/veiculo';

@Component({
  selector: 'app-veiculo-cadastro',
  imports: [CommonModule, FormsModule],
  templateUrl: './veiculo-cadastro.html',
  styleUrl: './veiculo-cadastro.css',
})
export class VeiculoCadastro {
  marca = '';
  modelo = '';
  ano: number | null = null;
  cor = '';
  quilometragem: number | null = null;
  preco: number | null = null;
  status: StatusVeiculo = 'DISPONIVEL';

  mensagemSucesso = '';
  mensagemErro = '';
  enviando = false;

  constructor(
    private readonly veiculoService: VeiculoService,
    private readonly changeDetector: ChangeDetectorRef,
  ) {}

  // Ricardo - envia o formulário de cadastro de veículo para o backend.
  cadastrar(formulario: NgForm): void {
    this.mensagemSucesso = '';
    this.mensagemErro = '';

    if (
      formulario.invalid ||
      this.ano === null ||
      this.preco === null
    ) {
      formulario.control.markAllAsTouched();
      this.mensagemErro = 'Preencha os campos obrigatórios corretamente.';
      return;
    }

    const dados: VeiculoCadastroRequest = {
      marca: this.marca.trim(),
      modelo: this.modelo.trim(),
      ano: this.ano,
      cor: this.cor.trim() || undefined,
      quilometragem: this.quilometragem ?? undefined,
      preco: this.preco,
      status: this.status,
    };

    this.enviando = true;

    this.veiculoService.cadastrar(dados).subscribe({
      next: () => {
        this.enviando = false;
        this.mensagemErro = '';
        this.mensagemSucesso = 'Veículo cadastrado com sucesso.';

        formulario.resetForm({
          marca: '',
          modelo: '',
          ano: null,
          cor: '',
          quilometragem: null,
          preco: null,
          status: 'DISPONIVEL',
        });

        this.marca = '';
        this.modelo = '';
        this.ano = null;
        this.cor = '';
        this.quilometragem = null;
        this.preco = null;
        this.status = 'DISPONIVEL';

        this.changeDetector.detectChanges();
      },

      error: (error: HttpErrorResponse) => {
        this.enviando = false;

        if (error.status === 400) {
          this.mensagemErro = this.obterMensagemErro(error);
        } else if (error.status === 401) {
          this.mensagemErro = 'Sua sessão expirou. Faça login novamente.';
        } else if (error.status === 403) {
          this.mensagemErro = 'Você não possui permissão para cadastrar veículos.';
        } else if (error.status === 0) {
          this.mensagemErro = 'Não foi possível conectar ao servidor.';
        } else {
          this.mensagemErro = 'Não foi possível cadastrar o veículo.';
        }

        this.changeDetector.detectChanges();
      },
    });
  }

  private obterMensagemErro(error: HttpErrorResponse): string {
    if (!error.error) {
      return 'Verifique os dados informados.';
    }

    if (typeof error.error === 'string') {
      return error.error;
    }

    const mensagens = Object.values(error.error)
      .filter(
        (mensagem): mensagem is string =>
          typeof mensagem === 'string',
      )
      .join(' ');

    return mensagens || 'Verifique os dados informados.';
  }
}