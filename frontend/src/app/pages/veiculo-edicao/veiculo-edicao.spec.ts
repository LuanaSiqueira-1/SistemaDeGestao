import {
  ComponentFixture,
  TestBed,
} from '@angular/core/testing';
import { NgForm } from '@angular/forms';
import {
  ActivatedRoute,
  provideRouter,
} from '@angular/router';
import { of } from 'rxjs';

import {
  VeiculoAtualizacaoRequest,
  VeiculoDetalheResponse,
  VeiculoResponse,
} from '../../core/models/veiculo';
import { VeiculoService } from '../../core/services/veiculo';
import { VeiculoEdicao } from './veiculo-edicao';

class VeiculoServiceMock {
  buscarPorIdChamadas: number[] = [];

  atualizarChamadas: Array<{
    id: number;
    dados: VeiculoAtualizacaoRequest;
  }> = [];

  buscarPorId(id: number) {
    this.buscarPorIdChamadas.push(id);

    const resposta: VeiculoDetalheResponse = {
      id,
      marca: 'Toyota',
      modelo: 'Corolla',
      ano: 2024,
      cor: 'Prata',
      quilometragem: 15000,
      preco: 129900,
      status: 'DISPONIVEL',
    };

    return of(resposta);
  }

  atualizar(
    id: number,
    dados: VeiculoAtualizacaoRequest,
  ) {
    this.atualizarChamadas.push({
      id,
      dados,
    });

    const resposta: VeiculoResponse = {
      id,
      marca: dados.marca,
      modelo: dados.modelo,
      ano: dados.ano,
      preco: dados.preco,
      status: dados.status,
    };

    return of(resposta);
  }
}

describe('VeiculoEdicao', () => {
  let component: VeiculoEdicao;
  let fixture: ComponentFixture<VeiculoEdicao>;
  let veiculoService: VeiculoServiceMock;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VeiculoEdicao],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: (chave: string) =>
                  chave === 'id'
                    ? '1'
                    : null,
              },
            },
          },
        },
        {
          provide: VeiculoService,
          useClass: VeiculoServiceMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(
      VeiculoEdicao,
    );

    component = fixture.componentInstance;

    veiculoService = TestBed.inject(
      VeiculoService,
    ) as unknown as VeiculoServiceMock;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load the vehicle data from the route id', () => {
    fixture.detectChanges();

    expect(
      veiculoService.buscarPorIdChamadas,
    ).toEqual([1]);

    expect(component.veiculoId).toBe(1);
    expect(component.marca).toBe('Toyota');
    expect(component.modelo).toBe('Corolla');
    expect(component.ano).toBe(2024);
    expect(component.cor).toBe('Prata');
    expect(component.quilometragem).toBe(15000);
    expect(component.preco).toBe(129900);
    expect(component.status).toBe('DISPONIVEL');
    expect(component.veiculoCarregado).toBe(true);
  });

  it('should update the loaded vehicle', () => {
    fixture.detectChanges();

    component.cor = 'Preto';
    component.preco = 135000;

    const formulario = {
      invalid: false,
      control: {
        markAllAsTouched: () => undefined,
      },
    } as unknown as NgForm;

    component.salvar(formulario);

    expect(
      veiculoService.atualizarChamadas.length,
    ).toBe(1);

    expect(
      veiculoService.atualizarChamadas[0].id,
    ).toBe(1);

    expect(
      veiculoService.atualizarChamadas[0].dados.cor,
    ).toBe('Preto');

    expect(
      veiculoService.atualizarChamadas[0].dados.preco,
    ).toBe(135000);

    expect(component.mensagemErro).toBe('');

    expect(component.mensagemSucesso).toBe(
      'Veículo atualizado com sucesso.',
    );
  });
});