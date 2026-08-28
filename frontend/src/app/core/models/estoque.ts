export interface EstoqueFiltros {
  marca?: string;
  categoria?: string;
  status?: string;
}

export interface DistribuicaoEstoque {
  nome: string;
  quantidadeTotal: number;
  quantidadeDisponivel: number;
}

export interface EstoqueResumoResponse {
  quantidadeTotal: number;
  quantidadeDisponivel: number;
  quantidadeIndisponivel: number;
  percentualDisponivel: number;
  valorTotalDisponivel: number;
  porMarca: DistribuicaoEstoque[];
  porModelo: DistribuicaoEstoque[];
  porCategoria: DistribuicaoEstoque[];
  porFaixaPreco: DistribuicaoEstoque[];
}