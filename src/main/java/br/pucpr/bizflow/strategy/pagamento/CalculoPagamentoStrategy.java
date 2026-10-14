package br.pucpr.bizflow.strategy.pagamento;

/** STRATEGY #2 — como o valor final é calculado conforme a forma de pagamento. */
public interface CalculoPagamentoStrategy {
    double calcular(double valorBase);

    String descricaoRegra();
}
