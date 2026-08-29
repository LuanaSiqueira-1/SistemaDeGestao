import {
  HttpClient,
  HttpParams,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

import {
  EstoqueFiltros,
  EstoqueResumoResponse,
} from '../models/estoque';

@Injectable({
  providedIn: 'root',
})
export class EstoqueService {
  private readonly apiUrl =
    `${environment.apiUrl}/api/estoque/resumo`;

  constructor(
    private readonly http: HttpClient,
  ) {}

  consultar(
    filtros: EstoqueFiltros = {},
  ): Observable<EstoqueResumoResponse> {
    let params = new HttpParams();

    const marca = filtros.marca?.trim();

    if (marca) {
      params = params.set('marca', marca);
    }

    const status = filtros.status?.trim();

    if (status) {
      params = params.set('status', status);
    }

    return this.http.get<EstoqueResumoResponse>(
      this.apiUrl,
      { params },
    );
  }
}