import {
  ClienteDetalheResponse,
  ClienteListagemResponse,
} from './cliente';

import {
  VeiculoListagemResponse,
  VeiculoResponse,
} from './veiculo';

export interface VendaRequestDTO {
  dataVenda: string;
  valor: number;
  veiculoId: number;
  clienteId: number;
}

export interface ClienteResumoDTO {
  id: number;
  nome: string;
}

export interface VeiculoResumoDTO {
  id: number;
}

export interface VendaResponseDTO {
  id: number;
  dataVenda: string;
  valor: number;
  cliente: ClienteResumoDTO;
  veiculo: VeiculoResumoDTO;
}

export interface VendaListagemResponse {
  id: number;
  dataVenda: string;
  valor: number;
  cliente: ClienteListagemResponse;
  veiculo: VeiculoListagemResponse;
}

export interface VendaDetalheResponse {
  id: number;
  dataVenda: string;
  valor: number;
  cliente: ClienteDetalheResponse;
  veiculo: VeiculoResponse;
}
