import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

import {
  ClienteAtualizacaoRequest,
  ClienteCadastroRequest,
  ClienteDetalheResponse,
  ClienteListagemResponse,
  ClienteResponse,
  HistoricoCompraResponse,
} from '../models/cliente';

import { PaginaResponse } from '../models/paginacao';

@Injectable({
  providedIn: 'root',
})
export class ClienteService {
  private readonly apiUrl = `${environment.apiUrl}/api/clientes`;

  constructor(private readonly http: HttpClient) {}

  cadastrar(
    dados: ClienteCadastroRequest,
  ): Observable<ClienteResponse> {
    return this.http.post<ClienteResponse>(
      this.apiUrl,
      dados,
    );
  }

  listar(
    page = 0,
    size = 10,
  ): Observable<PaginaResponse<ClienteListagemResponse>> {
    return this.pesquisar(undefined, undefined, page, size);
  }

  pesquisar(
    nome?: string,
    cpf?: string,
    page = 0,
    size = 10,
  ): Observable<PaginaResponse<ClienteListagemResponse>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    const nomeNormalizado = nome?.trim();
    const cpfNormalizado = cpf?.trim();

    if (nomeNormalizado) {
      params = params.set('nome', nomeNormalizado);
    }

    if (cpfNormalizado) {
      params = params.set('cpf', cpfNormalizado);
    }

    return this.http.get<PaginaResponse<ClienteListagemResponse>>(
      this.apiUrl,
      { params },
    );
  }

  buscarPorId(id: number): Observable<ClienteDetalheResponse> {
    return this.http.get<ClienteDetalheResponse>(
      `${this.apiUrl}/${id}`,
    );
  }

  atualizar(
    id: number,
    dados: ClienteAtualizacaoRequest,
  ): Observable<ClienteResponse> {
    return this.http.put<ClienteResponse>(
      `${this.apiUrl}/${id}`,
      dados,
    );
  }

  buscarHistoricoCompras(
    id: number,
  ): Observable<HistoricoCompraResponse[]> {
    return this.http.get<HistoricoCompraResponse[]>(
      `${this.apiUrl}/${id}/historico-compras`,
    );
  }
}