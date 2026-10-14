package br.pucpr.bizflow.dao;

import br.pucpr.bizflow.db.ConexaoBanco;
import br.pucpr.bizflow.model.Plano;
import br.pucpr.bizflow.model.ValidacaoException;
import br.pucpr.bizflow.variabilidade.GerenciadorVariabilidade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** VERSÃO SEM COMPACTAÇÃO: todo o SQL e o JDBC escritos à mão neste DAO. */
public class PlanoDAO implements Repositorio<Plano> {

    @Override
    public List<Plano> listar() {
        List<Plano> lista = new ArrayList<>();
        String sql = "SELECT * FROM plano WHERE negocio_id = ? ORDER BY id";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, negocioId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao listar plano: " + ex.getMessage(), ex);
        }
        return lista;
    }

    @Override
    public Plano buscarPorId(int id) {
        String sql = "SELECT * FROM plano WHERE id = ? AND negocio_id = ?";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, negocioId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao buscar plano: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Plano salvar(Plano e) {
        e.validar();
        e.setNegocioId(negocioId());
        if (e.isNovo()) {
            inserir(e);
        } else {
            atualizar(e);
        }
        return e;
    }

    private void inserir(Plano e) {
        String sql = "INSERT INTO plano (negocio_id, nome, valor_mensal, duracao_meses, aulas_por_semana) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, e.getNegocioId());
            ps.setString(2, e.getNome());
            ps.setDouble(3, e.getValorMensal());
            ps.setInt(4, e.getDuracaoMeses());
            ps.setInt(5, e.getAulasPorSemana());
            ps.executeUpdate();
            try (Statement st = conexao().createStatement();
                 ResultSet rs = st.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    e.setId(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao inserir plano: " + ex.getMessage(), ex);
        }
    }

    private void atualizar(Plano e) {
        String sql = "UPDATE plano SET nome = ?, valor_mensal = ?, duracao_meses = ?, aulas_por_semana = ? WHERE id = ? AND negocio_id = ?";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setString(1, e.getNome());
            ps.setDouble(2, e.getValorMensal());
            ps.setInt(3, e.getDuracaoMeses());
            ps.setInt(4, e.getAulasPorSemana());
            ps.setInt(5, e.getId());
            ps.setInt(6, e.getNegocioId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao atualizar plano: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM plano WHERE id = ? AND negocio_id = ?";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, negocioId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            if (String.valueOf(ex.getMessage()).contains("FOREIGN KEY")) {
                throw new ValidacaoException("Não é possível excluir: existem registros vinculados a este item.");
            }
            throw new RuntimeException("Erro ao excluir plano: " + ex.getMessage(), ex);
        }
    }

    private Plano mapear(ResultSet rs) throws SQLException {
        Plano e = new Plano();
        e.setId(rs.getInt("id"));
        e.setNegocioId(rs.getInt("negocio_id"));
        e.setNome(rs.getString("nome"));
        e.setValorMensal(rs.getDouble("valor_mensal"));
        e.setDuracaoMeses(rs.getInt("duracao_meses"));
        e.setAulasPorSemana(rs.getInt("aulas_por_semana"));
        return e;
    }

    private Connection conexao() {
        return ConexaoBanco.getInstancia().getConexao();
    }

    private int negocioId() {
        return GerenciadorVariabilidade.getInstancia().negocioAtualId();
    }
}
