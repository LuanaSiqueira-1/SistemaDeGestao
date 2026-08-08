import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import {
  VeiculoCadastroRequest,
  VeiculoListagemResponse,
} from '../models/veiculo';

@Injectable({
  providedIn: 'root',
})
export class VeiculoService {
  private readonly apiUrl = 'http://localhost:8080/api/veiculos';

  constructor(private readonly http: HttpClient) {}

  // Ricardo - envia os dados do veículo para o backend.
  cadastrar(
    dados: VeiculoCadastroRequest,
  ): Observable<VeiculoListagemResponse> {
    return this.http.post<VeiculoListagemResponse>(
      this.apiUrl,
      dados,
    );
  }

  // Ricardo - consulta os veículos cadastrados.
  listar(): Observable<VeiculoListagemResponse[]> {
    return this.http.get<VeiculoListagemResponse[]>(
      this.apiUrl,
    );
  }
}