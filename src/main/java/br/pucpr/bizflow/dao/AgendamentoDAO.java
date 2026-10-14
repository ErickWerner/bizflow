package br.pucpr.bizflow.dao;

import br.pucpr.bizflow.model.Agendamento;
import br.pucpr.bizflow.model.FormaPagamento;
import br.pucpr.bizflow.model.StatusAgendamento;
import br.pucpr.bizflow.variabilidade.GerenciadorVariabilidade;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AgendamentoDAO extends AbstractDAO<Agendamento> {
    @Override protected String tabela() { return "agendamento"; }

    @Override protected String[] colunas() {
        return new String[]{"cliente_id", "servico_id", "profissional_id", "data_hora",
                "status", "forma_pagamento", "valor_final"};
    }

    @Override protected Object[] valores(Agendamento a) {
        return new Object[]{a.getClienteId(), a.getServicoId(), a.getProfissionalId(), a.getDataHora(),
                a.getStatus(), a.getFormaPagamento(), a.getValorFinal()};
    }

    @Override protected Agendamento mapear(ResultSet rs) throws SQLException {
        Agendamento a = new Agendamento();
        a.setClienteId(rs.getInt("cliente_id"));
        a.setServicoId(rs.getInt("servico_id"));
        a.setProfissionalId(rs.getInt("profissional_id"));
        a.setDataHora(rs.getString("data_hora"));
        a.setStatus(StatusAgendamento.valueOf(rs.getString("status")));
        a.setFormaPagamento(FormaPagamento.valueOf(rs.getString("forma_pagamento")));
        a.setValorFinal(rs.getDouble("valor_final"));
        return a;
    }

    /**
     * Gancho do Template Method: além da validação padrão, aplica a
     * Strategy de agendamento do segmento (individual x em grupo).
     */
    @Override protected void validar(Agendamento a) {
        super.validar(a);
        GerenciadorVariabilidade.getInstancia().regraAgendamento().validar(a, listar());
    }
}
