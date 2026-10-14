package br.pucpr.bizflow.dao;

import br.pucpr.bizflow.db.ConexaoBanco;
import br.pucpr.bizflow.variabilidade.Modulo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;

/** Lê e grava a configuração de variabilidade (tabela modulo_ativo). */
public class ModuloDAO {

    public Map<Modulo, Boolean> carregar(int negocioId) {
        Map<Modulo, Boolean> mapa = new EnumMap<>(Modulo.class);
        String sql = "SELECT modulo, ativo FROM modulo_ativo WHERE negocio_id = ?";
        try (PreparedStatement ps = ConexaoBanco.getInstancia().getConexao().prepareStatement(sql)) {
            ps.setInt(1, negocioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        mapa.put(Modulo.valueOf(rs.getString("modulo")), rs.getInt("ativo") == 1);
                    } catch (IllegalArgumentException moduloRemovido) {
                        // módulo antigo que não existe mais no código: ignora
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar módulos: " + e.getMessage(), e);
        }
        return mapa;
    }

    public void salvar(int negocioId, Modulo modulo, boolean ativo) {
        String sql = "INSERT INTO modulo_ativo (negocio_id, modulo, ativo) VALUES (?, ?, ?) "
                + "ON CONFLICT(negocio_id, modulo) DO UPDATE SET ativo = excluded.ativo";
        try (PreparedStatement ps = ConexaoBanco.getInstancia().getConexao().prepareStatement(sql)) {
            ps.setInt(1, negocioId);
            ps.setString(2, modulo.name());
            ps.setInt(3, ativo ? 1 : 0);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar módulo: " + e.getMessage(), e);
        }
    }
}
