package br.pucpr.bizflow.dao;

import br.pucpr.bizflow.model.Servico;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ServicoDAO extends AbstractDAO<Servico> {
    @Override protected String tabela() { return "servico"; }

    @Override protected String[] colunas() { return new String[]{"nome", "categoria", "duracao_minutos", "preco"}; }

    @Override protected Object[] valores(Servico s) {
        return new Object[]{s.getNome(), s.getCategoria(), s.getDuracaoMinutos(), s.getPreco()};
    }

    @Override protected Servico mapear(ResultSet rs) throws SQLException {
        return new Servico(rs.getString("nome"), rs.getString("categoria"),
                rs.getInt("duracao_minutos"), rs.getDouble("preco"));
    }

    @Override protected String ordenacao() { return "nome"; }
}
