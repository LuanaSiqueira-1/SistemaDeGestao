import {
  ComponentFixture,
  TestBed,
} from '@angular/core/testing';
import { NgForm } from '@angular/forms';
import {
  provideRouter,
  Router,
} from '@angular/router';
import { of } from 'rxjs';
import { vi } from 'vitest';

import {
  Auth,
  RegisterRequest,
} from '../../core/services/auth';
import { Register } from './register';

class AuthMock {
  ultimoCadastro:
    RegisterRequest | null = null;

  register(dados: RegisterRequest) {
    this.ultimoCadastro = dados;

    return of({
      id: 1,
      nome: dados.nome,
      email: dados.email,
      role: 'USER' as const,
    });
  }
}

describe('Register', () => {
  let component: Register;
  let fixture: ComponentFixture<Register>;
  let authService: AuthMock;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Register],
      providers: [
        provideRouter([]),
        {
          provide: Auth,
          useClass: AuthMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(
      Register,
    );

    component = fixture.componentInstance;

    authService = TestBed.inject(
      Auth,
    ) as unknown as AuthMock;

    router = TestBed.inject(Router);

    vi.spyOn(
      router,
      'navigate',
    ).mockResolvedValue(true);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not render a role selector', () => {
    const elemento =
      fixture.nativeElement as HTMLElement;

    expect(
      elemento.querySelector('select'),
    ).toBeNull();

    expect(
      elemento.textContent,
    ).not.toContain('Administrador');

    expect(
      elemento.textContent,
    ).not.toContain('Tipo de usuário');
  });

  it('should register without sending a role', () => {
    component.nome =
      '  Marina Oliveira  ';

    component.email =
      'marina@teste.com';

    component.senha =
      'senha123';

    const formulario = {
      invalid: false,
      control: {
        markAllAsTouched: vi.fn(),
      },
      resetForm: vi.fn(),
    } as unknown as NgForm;

    component.cadastrar(formulario);

    expect(
      authService.ultimoCadastro,
    ).toEqual({
      nome: 'Marina Oliveira',
      email: 'marina@teste.com',
      senha: 'senha123',
    });

    expect(
      Object.prototype.hasOwnProperty.call(
        authService.ultimoCadastro ?? {},
        'role',
      ),
    ).toBe(false);

    expect(router.navigate)
      .toHaveBeenCalledWith(['/login']);
  });
});