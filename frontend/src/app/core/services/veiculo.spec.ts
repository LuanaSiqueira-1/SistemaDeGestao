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
  VeiculoAtualizacaoRequest,
  VeiculoDetalheResponse,
  VeiculoResponse,
} from '../models/veiculo';
import { VeiculoService } from './veiculo';

describe('VeiculoService', () => {
  let service: VeiculoService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        VeiculoService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(VeiculoService);
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

  it('should fetch a vehicle by id', () => {
    const resposta: VeiculoDetalheResponse = {
      id: 1,
      marca: 'Toyota',
      modelo: 'Corolla',
      ano: 2024,
      cor: 'Preto',
      quilometragem: 15000,
      preco: 129900,
      status: 'DISPONIVEL',
    };

    service.buscarPorId(1).subscribe(
      (resultado) => {
        expect(resultado).toEqual(resposta);
      },
    );

    const requisicao = httpTesting.expectOne(
      `${environment.apiUrl}/api/veiculos/1`,
    );

    expect(requisicao.request.method).toBe('GET');

    requisicao.flush(resposta);
  });

  it('should update a vehicle by id', () => {
    const dados: VeiculoAtualizacaoRequest = {
      marca: 'Toyota',
      modelo: 'Corolla',
      ano: 2024,
      cor: 'Preto',
      quilometragem: 15000,
      preco: 135000,
      status: 'DISPONIVEL',
    };

    const resposta: VeiculoResponse = {
      id: 1,
      marca: 'Toyota',
      modelo: 'Corolla',
      ano: 2024,
      preco: 135000,
      status: 'DISPONIVEL',
    };

    service.atualizar(1, dados).subscribe(
      (resultado) => {
        expect(resultado).toEqual(resposta);
      },
    );

    const requisicao = httpTesting.expectOne(
      `${environment.apiUrl}/api/veiculos/1`,
    );

    expect(requisicao.request.method).toBe('PUT');
    expect(requisicao.request.body).toEqual(dados);

    requisicao.flush(resposta);
  });
});