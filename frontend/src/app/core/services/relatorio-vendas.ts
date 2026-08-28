import {
  HttpClient,
  HttpParams,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

import {
  RelatorioVendasFiltros,
  RelatorioVendasResponse,
} from '../models/relatorio-vendas';

@Injectable({
  providedIn: 'root',
})
export class RelatorioVendasService {
  private readonly apiUrl =
    `${environment.apiUrl}/api/relatorios/vendas`;

  constructor(
    private readonly http: HttpClient,
  ) {}

  consultar(
    filtros: RelatorioVendasFiltros,
  ): Observable<RelatorioVendasResponse> {
    let params = new HttpParams()
      .set('ano', filtros.ano);

    if (filtros.semestre !== undefined) {
      params = params.set(
        'semestre',
        filtros.semestre,
      );
    }

    const marca = filtros.marca?.trim();

    if (marca) {
      params = params.set('marca', marca);
    }

    const categoria = filtros.categoria?.trim();

    if (categoria) {
      params = params.set(
        'categoria',
        categoria,
      );
    }

    return this.http.get<RelatorioVendasResponse>(
      this.apiUrl,
      { params },
    );
  }
}