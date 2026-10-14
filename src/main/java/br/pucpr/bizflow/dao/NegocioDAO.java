package br.pucpr.bizflow.dao;

import br.pucpr.bizflow.db.ConexaoBanco;
import br.pucpr.bizflow.model.Negocio;
import br.pucpr.bizflow.variabilidade.Segmento;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Lista os negócios (tenants) cadastrados — usado na tela de login. */
public class NegocioDAO {
    public List<Negocio> listar() {
        List<Negocio> lista = new ArrayList<>();
        String sql = "SELECT id, nome, segmento, cidade FROM negocio ORDER BY id";
        try (PreparedStatement ps = ConexaoBanco.getInstancia().getConexao().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Negocio(rs.getInt("id"), rs.getString("nome"),
                        Segmento.valueOf(rs.getString("segmento")), rs.getString("cidade")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar negócios: " + e.getMessage(), e);
        }
        return lista;
    }
}
