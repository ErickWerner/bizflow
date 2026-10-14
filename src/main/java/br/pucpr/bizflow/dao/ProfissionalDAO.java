package br.pucpr.bizflow.dao;

import br.pucpr.bizflow.model.Profissional;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfissionalDAO extends AbstractDAO<Profissional> {
    @Override protected String tabela() { return "profissional"; }

    @Override protected String[] colunas() { return new String[]{"nome", "especialidade", "telefone", "ativo"}; }

    @Override protected Object[] valores(Profissional p) {
        return new Object[]{p.getNome(), p.getEspecialidade(), p.getTelefone(), p.isAtivo()};
    }

    @Override protected Profissional mapear(ResultSet rs) throws SQLException {
        return new Profissional(rs.getString("nome"), rs.getString("especialidade"),
                rs.getString("telefone"), rs.getInt("ativo") == 1);
    }

    @Override protected String ordenacao() { return "nome"; }
}
