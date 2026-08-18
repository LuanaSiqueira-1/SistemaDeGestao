import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

import { PaginaResponse } from '../models/paginacao';

import {
  VendaDetalheResponse,
  VendaListagemResponse,
  VendaRequestDTO,
  VendaResponseDTO,
} from '../models/venda';

@Injectable({
  providedIn: 'root',
})
export class VendaService {
  private readonly apiUrl = `${environment.apiUrl}/api/vendas`;

  constructor(private readonly http: HttpClient) {}

  registrar(
    dados: VendaRequestDTO,
  ): Observable<VendaResponseDTO> {
    return this.http.post<VendaResponseDTO>(
      this.apiUrl,
      dados,
    );
  }

  listar(
    page = 0,
    size = 10,
  ): Observable<PaginaResponse<VendaListagemResponse>> {
    return this.pesquisar(undefined, undefined, page, size);
  }

  pesquisar(
    cliente?: string,
    veiculo?: string,
    page = 0,
    size = 10,
  ): Observable<PaginaResponse<VendaListagemResponse>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    const clienteNormalizado = cliente?.trim();
    const veiculoNormalizado = veiculo?.trim();

    if (clienteNormalizado) {
      params = params.set('cliente', clienteNormalizado);
    }

    if (veiculoNormalizado) {
      params = params.set('veiculo', veiculoNormalizado);
    }

    return this.http.get<PaginaResponse<VendaListagemResponse>>(
      this.apiUrl,
      { params },
    );
  }

  buscarPorId(id: number): Observable<VendaDetalheResponse> {
    return this.http.get<VendaDetalheResponse>(
      `${this.apiUrl}/${id}`,
    );
  }
}