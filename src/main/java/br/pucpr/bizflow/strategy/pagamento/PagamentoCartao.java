package br.pucpr.bizflow.strategy.pagamento;

public class PagamentoCartao implements CalculoPagamentoStrategy {
    private static final double TAXA = 0.03;

    @Override
    public double calcular(double valorBase) { return valorBase * (1 + TAXA); }

    @Override
    public String descricaoRegra() { return "3% de taxa da maquininha"; }
}
