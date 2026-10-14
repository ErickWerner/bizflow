package br.pucpr.bizflow.relatorio;

import br.pucpr.bizflow.dao.AgendamentoDAO;
import br.pucpr.bizflow.model.Agendamento;
import br.pucpr.bizflow.model.FormaPagamento;
import br.pucpr.bizflow.model.StatusAgendamento;

import java.util.EnumMap;
import java.util.Map;

public class RelatorioFaturamento extends RelatorioTemplate {
    private double total;

    @Override
    public String titulo() { return "Faturamento por forma de pagamento"; }

    @Override
    protected void corpo(StringBuilder sb) {
        Map<FormaPagamento, Double> porForma = new EnumMap<>(FormaPagamento.class);
        total = 0;
        for (Agendamento a : new AgendamentoDAO().listar()) {
            if (a.getStatus() == StatusAgendamento.CANCELADO) continue;
            porForma.merge(a.getFormaPagamento(), a.getValorFinal(), Double::sum);
            total += a.getValorFinal();
        }
        for (FormaPagamento f : FormaPagamento.values()) {
            sb.append(String.format("%-10s R$ %10.2f%n", f, porForma.getOrDefault(f, 0.0)));
        }
    }

    @Override
    protected void resumo(StringBuilder sb) {
        sb.append(String.format("TOTAL      R$ %10.2f%n", total));
    }
}
