package br.pucpr.bizflow.strategy.pagamento;

public class PagamentoDinheiro implements CalculoPagamentoStrategy {

    /** Arredonda para baixo em múltiplos de R$ 0,50 (facilita o troco). */
    @Override
    public double calcular(double valorBase) { return Math.floor(valorBase * 2) / 2.0; }

    @Override
    public String descricaoRegra() { return "arredondado para baixo em R$ 0,50"; }
}
