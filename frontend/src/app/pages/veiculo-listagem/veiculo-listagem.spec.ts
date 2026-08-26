import {
  ComponentFixture,
  TestBed,
} from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import {
  VeiculoListagemResponse,
} from '../../core/models/veiculo';
import { VeiculoService } from '../../core/services/veiculo';
import { VeiculoListagem } from './veiculo-listagem';

class VeiculoServiceMock {
  listar() {
    const veiculos: VeiculoListagemResponse[] = [
      {
        id: 1,
        marca: 'Toyota',
        modelo: 'Corolla',
        ano: 2024,
        preco: 129900,
        status: 'DISPONIVEL',
      },
    ];

    return of(veiculos);
  }
}

describe('VeiculoListagem', () => {
  let component: VeiculoListagem;
  let fixture: ComponentFixture<VeiculoListagem>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VeiculoListagem],
      providers: [
        provideRouter([]),
        {
          provide: VeiculoService,
          useClass: VeiculoServiceMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(
      VeiculoListagem,
    );

    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should list the vehicles returned by the service', () => {
    fixture.detectChanges();

    expect(component.veiculos.length).toBe(1);
    expect(component.veiculosFiltrados.length).toBe(1);

    expect(component.veiculos[0].marca).toBe(
      'Toyota',
    );

    expect(component.veiculos[0].modelo).toBe(
      'Corolla',
    );
  });

  it('should render the edit link for the vehicle', () => {
    fixture.detectChanges();

    const elemento =
      fixture.nativeElement as HTMLElement;

    const link =
      elemento.querySelector<HTMLAnchorElement>(
        'a.edit-button',
      );

    expect(link).not.toBeNull();

    expect(
      link?.getAttribute('href'),
    ).toBe('/veiculos/1/editar');
  });
});