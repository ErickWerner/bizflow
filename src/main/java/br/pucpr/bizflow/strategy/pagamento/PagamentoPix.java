package br.pucpr.bizflow.strategy.pagamento;

public class PagamentoPix implements CalculoPagamentoStrategy {
    private static final double DESCONTO = 0.05;

    @Override
    public double calcular(double valorBase) { return valorBase * (1 - DESCONTO); }

    @Override
    public String descricaoRegra() { return "5% de desconto no Pix"; }
}
