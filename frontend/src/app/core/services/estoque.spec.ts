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
  EstoqueResumoResponse,
} from '../models/estoque';

import { EstoqueService } from './estoque';

describe('EstoqueService', () => {
  let service: EstoqueService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        EstoqueService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(EstoqueService);

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

  it('should send stock filters as query parameters', () => {
    const resposta: EstoqueResumoResponse = {
      quantidadeTotal: 10,
      quantidadeDisponivel: 8,
      quantidadeIndisponivel: 2,
      percentualDisponivel: 80,
      valorTotalDisponivel: 720000,
      porMarca: [],
      porModelo: [],
      porCategoria: [],
      porFaixaPreco: [],
    };

    service.consultar({
      marca: ' Toyota ',
      categoria: ' Sedan ',
      status: ' DISPONIVEL ',
    }).subscribe(
      (resultado) => {
        expect(resultado).toEqual(resposta);
      },
    );

    const requisicao = httpTesting.expectOne(
      (request) =>
        request.url ===
          `${environment.apiUrl}/api/estoque/resumo` &&
        request.params.get('marca') === 'Toyota' &&
        request.params.get('categoria') === 'Sedan' &&
        request.params.get('status') === 'DISPONIVEL',
    );

    expect(requisicao.request.method).toBe('GET');

    requisicao.flush(resposta);
  });

  it('should request the stock summary without optional filters', () => {
    service.consultar().subscribe();

    const requisicao = httpTesting.expectOne(
      `${environment.apiUrl}/api/estoque/resumo`,
    );

    expect(requisicao.request.method).toBe('GET');

    expect(
      requisicao.request.params.keys().length,
    ).toBe(0);

    requisicao.flush({
      quantidadeTotal: 0,
      quantidadeDisponivel: 0,
      quantidadeIndisponivel: 0,
      percentualDisponivel: 0,
      valorTotalDisponivel: 0,
      porMarca: [],
      porModelo: [],
      porCategoria: [],
      porFaixaPreco: [],
    });
  });
});