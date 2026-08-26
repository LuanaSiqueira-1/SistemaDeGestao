import {
  ComponentFixture,
  TestBed,
} from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import {
  ClienteListagemResponse,
} from '../../core/models/cliente';
import { ClienteService } from '../../core/services/cliente';
import { ClienteListagem } from './cliente-listagem';

class ClienteServiceMock {
  pesquisar() {
    const clientes: ClienteListagemResponse[] = [
      {
        id: 1,
        nome: 'Marina Oliveira',
        cpf: '12345678901',
      },
    ];

    return of({
      content: clientes,
      totalPages: 1,
      totalElements: 1,
      number: 0,
    });
  }
}

describe('ClienteListagem', () => {
  let component: ClienteListagem;
  let fixture: ComponentFixture<ClienteListagem>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClienteListagem],
      providers: [
        provideRouter([]),
        {
          provide: ClienteService,
          useClass: ClienteServiceMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(
      ClienteListagem,
    );

    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should list clients returned by the service', () => {
    fixture.detectChanges();

    expect(component.clientes.length).toBe(1);
    expect(component.totalElementos).toBe(1);

    expect(component.clientes[0].nome).toBe(
      'Marina Oliveira',
    );
  });

  it('should render the edit link for the client', () => {
    fixture.detectChanges();

    const elemento =
      fixture.nativeElement as HTMLElement;

    const link =
      elemento.querySelector<HTMLAnchorElement>(
        'a.edit-link',
      );

    expect(link).not.toBeNull();

    expect(
      link?.getAttribute('href'),
    ).toBe('/clientes/1/editar');
  });
});