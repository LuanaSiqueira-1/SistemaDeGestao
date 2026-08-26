import {
  ComponentFixture,
  TestBed,
} from '@angular/core/testing';
import {
  ActivatedRoute,
  provideRouter,
} from '@angular/router';
import { of } from 'rxjs';

import {
  ClienteDetalheResponse,
  HistoricoCompraResponse,
} from '../../core/models/cliente';
import { ClienteService } from '../../core/services/cliente';
import { ClienteDetalhe } from './cliente-detalhe';

class ClienteServiceMock {
  buscarPorIdChamadas: number[] = [];
  historicoChamadas: number[] = [];

  buscarPorId(id: number) {
    this.buscarPorIdChamadas.push(id);

    const resposta: ClienteDetalheResponse = {
      id,
      nome: 'Marina Oliveira',
      cpf: '12345678901',
      telefone: '87988888888',
      email: 'marina.cliente@teste.com',
    };

    return of(resposta);
  }

  buscarHistoricoCompras(id: number) {
    this.historicoChamadas.push(id);

    const resposta: HistoricoCompraResponse[] = [
      {
        veiculo: {
          id: 2,
          marca: 'Honda',
          modelo: 'Civic',
          ano: 2024,
        },
        dataVenda: '2026-08-20',
        valor: 90000,
      },
    ];

    return of(resposta);
  }
}

describe('ClienteDetalhe', () => {
  let component: ClienteDetalhe;
  let fixture: ComponentFixture<ClienteDetalhe>;
  let clienteService: ClienteServiceMock;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClienteDetalhe],
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
      ClienteDetalhe,
    );

    component = fixture.componentInstance;

    clienteService = TestBed.inject(
      ClienteService,
    ) as unknown as ClienteServiceMock;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load the client and purchase history', () => {
    fixture.detectChanges();

    expect(
      clienteService.buscarPorIdChamadas,
    ).toEqual([1]);

    expect(
      clienteService.historicoChamadas,
    ).toEqual([1]);

    expect(component.cliente?.nome).toBe(
      'Marina Oliveira',
    );

    expect(
      component.historicoCompras.length,
    ).toBe(1);

    expect(
      component.historicoCompras[0]
        .veiculo.modelo,
    ).toBe('Civic');
  });

  it('should format a string sale date', () => {
    expect(
      component.formatarDataVenda(
        '2026-08-20',
      ),
    ).toBe('20/08/2026');
  });

  it('should format an array sale date', () => {
    expect(
      component.formatarDataVenda(
        [2026, 8, 20],
      ),
    ).toBe('20/08/2026');
  });

  it('should render the purchase history', () => {
    fixture.detectChanges();

    const elemento =
      fixture.nativeElement as HTMLElement;

    expect(
      elemento.textContent,
    ).toContain('Honda');

    expect(
      elemento.textContent,
    ).toContain('Civic');

    expect(
      elemento.textContent,
    ).toContain('20/08/2026');
  });
});