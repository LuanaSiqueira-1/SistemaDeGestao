import {
  provideHttpClient,
} from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { environment } from '../../../environments/environment';

import {
  RelatorioVendasResponse,
} from '../models/relatorio-vendas';

import {
  RelatorioVendasService,
} from './relatorio-vendas';

describe('RelatorioVendasService', () => {
  let service: RelatorioVendasService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        RelatorioVendasService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(
      RelatorioVendasService,
    );

    httpTesting = TestBed.inject(
      HttpTestingController,
    );
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should create the service', () => {
    expect(service).toBeTruthy();
  });

  it('should send report filters as query parameters', () => {
    const resposta: RelatorioVendasResponse = {
      ano: 2026,
      semestre: 1,
      quantidadeVendas: 2,
      valorTotal: 180000,
      ticketMedio: 90000,
      vendasPorMarca: [],
      vendasPorModelo: [],
      maisVendidos: [],
      menosVendidos: [],
    };

    service.consultar({
      ano: 2026,
      semestre: 1,
      marca: ' Toyota ',
    }).subscribe(
      (resultado) => {
        expect(resultado).toEqual(resposta);
      },
    );

    const requisicao = httpTesting.expectOne(
      (request) =>
        request.url ===
          `${environment.apiUrl}/api/relatorios/vendas` &&
        request.params.get('ano') === '2026' &&
        request.params.get('semestre') === '1' &&
        request.params.get('marca') === 'Toyota',
    );

    expect(requisicao.request.method).toBe('GET');

    requisicao.flush(resposta);
  });

  it('should omit empty optional filters', () => {
    service.consultar({
      ano: 2026,
      marca: '   ',
      categoria: '',
    }).subscribe();

    const requisicao = httpTesting.expectOne(
      `${environment.apiUrl}/api/relatorios/vendas?ano=2026`,
    );

    expect(
      requisicao.request.params.has('semestre'),
    ).toBe(false);

    expect(
      requisicao.request.params.has('marca'),
    ).toBe(false);

    expect(
      requisicao.request.params.has('categoria'),
    ).toBe(false);

    requisicao.flush({
      ano: 2026,
      semestre: null,
      quantidadeVendas: 0,
      valorTotal: 0,
      ticketMedio: 0,
      vendasPorMarca: [],
      vendasPorModelo: [],
      maisVendidos: [],
      menosVendidos: [],
    });
  });
});