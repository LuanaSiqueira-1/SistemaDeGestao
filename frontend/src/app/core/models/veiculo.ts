// Ricardo - contratos usados pelo frontend nas funcionalidades de veículos.

export type StatusVeiculo =
  | 'DISPONIVEL'
  | 'VENDIDO'
  | 'EM_MANUTENCAO';

export interface VeiculoCadastroRequest {
  marca: string;
  modelo: string;
  ano: number;
  cor?: string;
  quilometragem?: number;
  preco: number;
  status: StatusVeiculo;
}

export interface VeiculoListagemResponse {
  id: number;
  marca: string;
  modelo: string;
  ano: number;
  preco: number;
  status: StatusVeiculo;
}

export interface VeiculoResponse {
  id: number;
  marca: string;
  modelo: string;
  ano: number;
  preco: number;
  status: StatusVeiculo;
}
