export interface ClienteCadastroRequest {
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
