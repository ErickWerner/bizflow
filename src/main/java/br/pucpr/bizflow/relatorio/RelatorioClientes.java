package br.pucpr.bizflow.relatorio;

import br.pucpr.bizflow.dao.ClienteDAO;
import br.pucpr.bizflow.model.Cliente;
import br.pucpr.bizflow.variabilidade.GerenciadorVariabilidade;

import java.util.List;

public class RelatorioClientes extends RelatorioTemplate {
    private List<Cliente> clientes;

    @Override
    public String titulo() {
        return "Cadastro de " + GerenciadorVariabilidade.getInstancia().rotuloCliente() + "s";
    }

    @Override
    protected void corpo(StringBuilder sb) {
        clientes = new ClienteDAO().listar();
        for (Cliente c : clientes) {
            sb.append(String.format("%-25s %-18s %s%n", c.getNome(), c.getTelefone(),
                    c.getEmail() == null ? "" : c.getEmail()));
        }
    }

    @Override
    protected void resumo(StringBuilder sb) {
        long semEmail = clientes.stream().filter(c -> c.getEmail() == null || c.getEmail().isBlank()).count();
        sb.append("Total: ").append(clientes.size()).append(" | Sem e-mail: ").append(semEmail).append('\n');
    }
}
