package br.pucpr.bizflow.model;

import br.pucpr.bizflow.strategy.pagamento.CalculoPagamentoStrategy;
import br.pucpr.bizflow.strategy.pagamento.PagamentoCartao;
import br.pucpr.bizflow.strategy.pagamento.PagamentoDinheiro;
import br.pucpr.bizflow.strategy.pagamento.PagamentoPix;

/** Cada forma de pagamento sabe qual Strategy de cálculo usar. */
public enum FormaPagamento {
    PIX("Pix"),
    CARTAO("Cartão"),
    DINHEIRO("Dinheiro");

    private final String descricao;

    FormaPagamento(String descricao) { this.descricao = descricao; }

    public CalculoPagamentoStrategy estrategia() {
        return switch (this) {
            case PIX -> new PagamentoPix();
            case CARTAO -> new PagamentoCartao();
            case DINHEIRO -> new PagamentoDinheiro();
        };
    }

    @Override
    public String toString() { return descricao; }
}
