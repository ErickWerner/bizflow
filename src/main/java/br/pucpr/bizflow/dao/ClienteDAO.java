package br.pucpr.bizflow.dao;

import br.pucpr.bizflow.model.Cliente;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteDAO extends AbstractDAO<Cliente> {
    @Override protected String tabela() { return "cliente"; }

    @Override protected String[] colunas() { return new String[]{"nome", "telefone", "email", "observacoes"}; }

    @Override protected Object[] valores(Cliente c) {
        return new Object[]{c.getNome(), c.getTelefone(), c.getEmail(), c.getObservacoes()};
    }

    @Override protected Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(rs.getString("nome"), rs.getString("telefone"),
                rs.getString("email"), rs.getString("observacoes"));
    }

    @Override protected String ordenacao() { return "nome"; }
}
