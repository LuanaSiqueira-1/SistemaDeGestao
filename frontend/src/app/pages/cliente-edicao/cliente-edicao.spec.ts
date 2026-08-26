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
  ClienteAtualizacaoRequest,
  ClienteDetalheResponse,
  ClienteResponse,
} from '../../core/models/cliente';
import { ClienteService } from '../../core/services/cliente';
import { ClienteEdicao } from './cliente-edicao';

class ClienteServiceMock {
  buscarPorIdChamadas: number[] = [];

  atualizarChamadas: Array<{
    id: number;
    dados: ClienteAtualizacaoRequest;
  }> = [];

  buscarPorId(id: number) {
    this.buscarPorIdChamadas.push(id);

    const resposta: ClienteDetalheResponse = {
      id,
      nome: 'Marina Oliveira',
      cpf: '12345678901',
      telefone: '87999999999',
      email: 'marina@teste.com',
    };

    return of(resposta);
  }

  atualizar(
    id: number,
    dados: ClienteAtualizacaoRequest,
  ) {
    this.atualizarChamadas.push({
      id,
      dados,
    });

    const resposta: ClienteResponse = {
      id,
      ...dados,
    };

    return of(resposta);
  }
}

describe('ClienteEdicao', () => {
  let component: ClienteEdicao;
  let fixture: ComponentFixture<ClienteEdicao>;
  let clienteService: ClienteServiceMock;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClienteEdicao],
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
          provide: ClienteService,
          useClass: ClienteServiceMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(
      ClienteEdicao,
    );

    component = fixture.componentInstance;

    clienteService = TestBed.inject(
      ClienteService,
    ) as unknown as ClienteServiceMock;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load the client data from the route id', () => {
    fixture.detectChanges();

    expect(
      clienteService.buscarPorIdChamadas,
    ).toEqual([1]);

    expect(component.clienteId).toBe(1);
    expect(component.nome).toBe('Marina Oliveira');
    expect(component.cpf).toBe('12345678901');
    expect(component.telefone).toBe('87999999999');
    expect(component.email).toBe('marina@teste.com');
    expect(component.clienteCarregado).toBe(true);
  });

  it('should update the loaded client', () => {
    fixture.detectChanges();

    component.telefone = '87988888888';
    component.email =
      'marina.oliveira@teste.com';

    const formulario = {
      invalid: false,
      control: {
        markAllAsTouched: () => undefined,
      },
    } as unknown as NgForm;

    component.salvar(formulario);

    expect(
      clienteService.atualizarChamadas.length,
    ).toBe(1);

    expect(
      clienteService.atualizarChamadas[0].id,
    ).toBe(1);

    expect(
      clienteService.atualizarChamadas[0]
        .dados.telefone,
    ).toBe('87988888888');

    expect(
      clienteService.atualizarChamadas[0]
        .dados.email,
    ).toBe('marina.oliveira@teste.com');

    expect(component.mensagemErro).toBe('');

    expect(component.mensagemSucesso).toBe(
      'Cliente atualizado com sucesso.',
    );
  });
});