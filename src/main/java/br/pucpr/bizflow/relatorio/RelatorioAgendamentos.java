package br.pucpr.bizflow.relatorio;

import br.pucpr.bizflow.dao.AgendamentoDAO;
import br.pucpr.bizflow.dao.ClienteDAO;
import br.pucpr.bizflow.dao.ServicoDAO;
import br.pucpr.bizflow.model.Agendamento;
import br.pucpr.bizflow.model.Cliente;
import br.pucpr.bizflow.model.Servico;

import java.util.List;

public class RelatorioAgendamentos extends RelatorioTemplate {
    private List<Agendamento> agendamentos;

    @Override
    public String titulo() { return "Agenda"; }

    @Override
    protected void corpo(StringBuilder sb) {
        agendamentos = new AgendamentoDAO().listar();
        ClienteDAO clientes = new ClienteDAO();
        ServicoDAO servicos = new ServicoDAO();
        for (Agendamento a : agendamentos) {
            Cliente c = clientes.buscarPorId(a.getClienteId());
            Servico s = servicos.buscarPorId(a.getServicoId());
            sb.append(String.format("%-17s %-20s %-20s %s%n", a.getDataHora(),
                    c == null ? "?" : c.getNome(), s == null ? "?" : s.getNome(), a.getStatus()));
        }
    }

    @Override
    protected void resumo(StringBuilder sb) {
        long cancelados = agendamentos.stream().filter(a -> !a.isAtivo()).count();
        sb.append("Agendamentos: ").append(agendamentos.size())
          .append(" | Cancelados: ").append(cancelados).append('\n');
    }
}
