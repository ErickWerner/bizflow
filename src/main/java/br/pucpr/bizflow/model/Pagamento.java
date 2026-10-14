package br.pucpr.bizflow.model;

import br.pucpr.bizflow.strategy.pagamento.CalculoPagamentoStrategy;

/**
 * Contexto do padrão Strategy de pagamento: o Pagamento não sabe
 * calcular taxa/desconto — ele delega para a estratégia recebida.
 */
public class Pagamento {
    private final double valorBase;
    private final FormaPagamento forma;
    private final CalculoPagamentoStrategy estrategia;

    public Pagamento(double valorBase, FormaPagamento forma) {
        this.valorBase = valorBase;
        this.forma = forma;
        this.estrategia = forma.estrategia();
    }

    public double valorFinal() { return estrategia.calcular(valorBase); }

    public String resumo() {
        return String.format("%s: base R$ %.2f -> final R$ %.2f (%s)",
                forma, valorBase, valorFinal(), estrategia.descricaoRegra());
    }

    public double getValorBase() { return valorBase; }
    public FormaPagamento getForma() { return forma; }
}
