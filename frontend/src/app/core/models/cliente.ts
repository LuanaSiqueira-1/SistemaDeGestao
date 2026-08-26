export interface ClienteCadastroRequest {
  nome: string;
  cpf: string;
  telefone: string;
  email: string;
}

export interface ClienteAtualizacaoRequest {
  nome: string;
  cpf: string;
  telefone: string;
  email: string;
}

export interface ClienteResponse {
  id: number;
  nome: string;
  cpf: string;
  telefone: string;
  email: string;
}

export interface ClienteListagemResponse {
  id: number;
  nome: string;
  cpf: string;
}

export interface ClienteDetalheResponse {
  id: number;
  nome: string;
  cpf: string;
  telefone: string;
  email: string;
}

export interface VeiculoHistoricoResponse {
  id: number;
  marca: string;
  modelo: string;
  ano: number;
}

export type DataVendaHistorico =
  | string
  | [number, number, number];

export interface HistoricoCompraResponse {
  veiculo: VeiculoHistoricoResponse;
  dataVenda: DataVendaHistorico;
  valor: number;
}