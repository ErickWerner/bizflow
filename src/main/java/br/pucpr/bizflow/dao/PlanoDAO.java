package br.pucpr.bizflow.dao;

import br.pucpr.bizflow.model.Plano;

import java.sql.ResultSet;
import java.sql.SQLException;

/** DAO do módulo EXCLUSIVO Academia — reaproveita 100% do AbstractDAO. */
public class PlanoDAO extends AbstractDAO<Plano> {
    @Override protected String tabela() { return "plano"; }

    @Override protected String[] colunas() {
        return new String[]{"nome", "valor_mensal", "duracao_meses", "aulas_por_semana"};
    }

    @Override protected Object[] valores(Plano p) {
        return new Object[]{p.getNome(), p.getValorMensal(), p.getDuracaoMeses(), p.getAulasPorSemana()};
    }

    @Override protected Plano mapear(ResultSet rs) throws SQLException {
        Plano p = new Plano();
        p.setNome(rs.getString("nome"));
        p.setValorMensal(rs.getDouble("valor_mensal"));
        p.setDuracaoMeses(rs.getInt("duracao_meses"));
        p.setAulasPorSemana(rs.getInt("aulas_por_semana"));
        return p;
    }
}
