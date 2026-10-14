package br.pucpr.bizflow.dao;

import br.pucpr.bizflow.model.Pet;

import java.sql.ResultSet;
import java.sql.SQLException;

/** DAO do módulo EXCLUSIVO Petshop — reaproveita 100% do AbstractDAO. */
public class PetDAO extends AbstractDAO<Pet> {
    @Override protected String tabela() { return "pet"; }

    @Override protected String[] colunas() {
        return new String[]{"tutor_id", "nome", "especie", "raca", "ultima_vacina"};
    }

    @Override protected Object[] valores(Pet p) {
        return new Object[]{p.getTutorId(), p.getNome(), p.getEspecie(), p.getRaca(), p.getUltimaVacina()};
    }

    @Override protected Pet mapear(ResultSet rs) throws SQLException {
        Pet p = new Pet();
        p.setTutorId(rs.getInt("tutor_id"));
        p.setNome(rs.getString("nome"));
        p.setEspecie(rs.getString("especie"));
        p.setRaca(rs.getString("raca"));
        p.setUltimaVacina(rs.getString("ultima_vacina"));
        return p;
    }
}
