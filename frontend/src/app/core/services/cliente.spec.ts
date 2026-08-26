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
  ClienteAtualizacaoRequest,
  ClienteDetalheResponse,
  ClienteResponse,
} from '../models/cliente';
import { ClienteService } from './cliente';

describe('ClienteService', () => {
  let service: ClienteService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ClienteService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(ClienteService);

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

  it('should fetch a client by id', () => {
    const resposta: ClienteDetalheResponse = {
      id: 1,
      nome: 'Marina Oliveira',
      cpf: '12345678901',
      telefone: '87999999999',
      email: 'marina@teste.com',
    };

    service.buscarPorId(1).subscribe(
      (resultado) => {
        expect(resultado).toEqual(resposta);
      },
    );

    const requisicao = httpTesting.expectOne(
      `${environment.apiUrl}/api/clientes/1`,
    );

    expect(requisicao.request.method).toBe('GET');

    requisicao.flush(resposta);
  });

  it('should update a client by id', () => {
    const dados: ClienteAtualizacaoRequest = {
      nome: 'Marina Oliveira',
      cpf: '12345678901',
      telefone: '87988888888',
      email: 'marina.oliveira@teste.com',
    };

    const resposta: ClienteResponse = {
      id: 1,
      ...dados,
    };

    service.atualizar(1, dados).subscribe(
      (resultado) => {
        expect(resultado).toEqual(resposta);
      },
    );

    const requisicao = httpTesting.expectOne(
      `${environment.apiUrl}/api/clientes/1`,
    );

    expect(requisicao.request.method).toBe('PUT');
    expect(requisicao.request.body).toEqual(dados);

    requisicao.flush(resposta);
  });
});