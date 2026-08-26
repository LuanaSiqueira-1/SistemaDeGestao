import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

import {
  VeiculoAtualizacaoRequest,
  VeiculoCadastroRequest,
  VeiculoDetalheResponse,
  VeiculoListagemResponse,
  VeiculoResponse,
} from '../models/veiculo';

@Injectable({
  providedIn: 'root',
})
export class VeiculoService {
  private readonly apiUrl = `${environment.apiUrl}/api/veiculos`;

  constructor(private readonly http: HttpClient) {}

  // Envia os dados do veículo para o backend.
  cadastrar(
    dados: VeiculoCadastroRequest,
  ): Observable<VeiculoResponse> {
    return this.http.post<VeiculoResponse>(
      this.apiUrl,
      dados,
    );
  }

  // Consulta os veículos cadastrados.
  listar(): Observable<VeiculoListagemResponse[]> {
    return this.http.get<VeiculoListagemResponse[]>(
      this.apiUrl,
    );
  }

  // Consulta um veículo específico com os dados completos.
  buscarPorId(id: number): Observable<VeiculoDetalheResponse> {
    return this.http.get<VeiculoDetalheResponse>(
      `${this.apiUrl}/${id}`,
    );
  }

  // Atualiza os dados de um veículo existente.
  atualizar(
    id: number,
    dados: VeiculoAtualizacaoRequest,
  ): Observable<VeiculoResponse> {
    return this.http.put<VeiculoResponse>(
      `${this.apiUrl}/${id}`,
      dados,
    );
  }
}