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
  Auth,
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  RegisterResponse,
} from './auth';

describe('Auth', () => {
  let service: Auth;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        Auth,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(Auth);

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

  it('should register without sending a role', () => {
    const dados: RegisterRequest = {
      nome: 'Marina Oliveira',
      email: 'marina@teste.com',
      senha: 'senha123',
    };

    const resposta: RegisterResponse = {
      id: 1,
      nome: 'Marina Oliveira',
      email: 'marina@teste.com',
      role: 'USER',
    };

    service.register(dados).subscribe(
      (resultado) => {
        expect(resultado).toEqual(resposta);
      },
    );

    const requisicao = httpTesting.expectOne(
      `${environment.apiUrl}/api/auth/register`,
    );

    expect(requisicao.request.method)
      .toBe('POST');

    expect(requisicao.request.body)
      .toEqual(dados);

    expect(
      Object.prototype.hasOwnProperty.call(
        requisicao.request.body,
        'role',
      ),
    ).toBe(false);

    requisicao.flush(resposta);
  });

  it('should send login credentials normally', () => {
    const dados: LoginRequest = {
      email: 'marina@teste.com',
      password: 'senha123',
    };

    const resposta: AuthResponse = {
      token: 'token-jwt',
      nome: 'Marina Oliveira',
      email: 'marina@teste.com',
      role: 'USER',
    };

    service.login(dados).subscribe(
      (resultado) => {
        expect(resultado).toEqual(resposta);
      },
    );

    const requisicao = httpTesting.expectOne(
      `${environment.apiUrl}/api/auth/login`,
    );

    expect(requisicao.request.method)
      .toBe('POST');

    expect(requisicao.request.body)
      .toEqual(dados);

    requisicao.flush(resposta);
  });
});