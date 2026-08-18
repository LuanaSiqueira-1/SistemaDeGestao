import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

import {
  VeiculoCadastroRequest,
  VeiculoListagemResponse,
} from '../models/veiculo';

@Injectable({
  providedIn: 'root',
})
export class VeiculoService {
  private readonly apiUrl = `${environment.apiUrl}/api/veiculos`;

  constructor(private readonly http: HttpClient) {}

  // Envia os dados do veiculo para o backend.
  cadastrar(
    dados: VeiculoCadastroRequest,
  ): Observable<VeiculoListagemResponse> {
    return this.http.post<VeiculoListagemResponse>(
      this.apiUrl,
      dados,
    );
  }

  // Consulta os veiculos cadastrados.
  listar(): Observable<VeiculoListagemResponse[]> {
    return this.http.get<VeiculoListagemResponse[]>(
      this.apiUrl,
    );
  }
}