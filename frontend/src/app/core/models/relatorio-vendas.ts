export interface RelatorioVendasFiltros {
  ano: number;
  semestre?: 1 | 2;
  marca?: string;
}

export interface VendasPorMarca {
  marca: string;
  quantidade: number;
  valorTotal: number;
}

export interface VendasPorModelo {
  marca: string;
  modelo: string;
  quantidade: number;
  valorTotal: number;
}

export interface RankingModelo {
  marca: string;
  modelo: string;
  quantidade: number;
}

export interface RelatorioVendasResponse {
  ano: number;
  semestre: number | null;
  quantidadeVendas: number;
  valorTotal: number;
  ticketMedio: number;
  vendasPorMarca: VendasPorMarca[];
  vendasPorModelo: VendasPorModelo[];
  maisVendidos: RankingModelo[];
  menosVendidos: RankingModelo[];
}