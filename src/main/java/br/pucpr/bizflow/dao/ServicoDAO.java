package br.pucpr.bizflow.dao;

import br.pucpr.bizflow.db.ConexaoBanco;
import br.pucpr.bizflow.model.Servico;
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
public class ServicoDAO implements Repositorio<Servico> {

    @Override
    public List<Servico> listar() {
        List<Servico> lista = new ArrayList<>();
        String sql = "SELECT * FROM servico WHERE negocio_id = ? ORDER BY nome";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, negocioId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao listar servico: " + ex.getMessage(), ex);
        }
        return lista;
    }

    @Override
    public Servico buscarPorId(int id) {
        String sql = "SELECT * FROM servico WHERE id = ? AND negocio_id = ?";
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
            throw new RuntimeException("Erro ao buscar servico: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Servico salvar(Servico e) {
        e.validar();
        e.setNegocioId(negocioId());
        if (e.isNovo()) {
            inserir(e);
        } else {
            atualizar(e);
        }
        return e;
    }

    private void inserir(Servico e) {
        String sql = "INSERT INTO servico (negocio_id, nome, categoria, duracao_minutos, preco) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, e.getNegocioId());
            ps.setString(2, e.getNome());
            ps.setString(3, e.getCategoria());
            ps.setInt(4, e.getDuracaoMinutos());
            ps.setDouble(5, e.getPreco());
            ps.executeUpdate();
            try (Statement st = conexao().createStatement();
                 ResultSet rs = st.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    e.setId(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao inserir servico: " + ex.getMessage(), ex);
        }
    }

    private void atualizar(Servico e) {
        String sql = "UPDATE servico SET nome = ?, categoria = ?, duracao_minutos = ?, preco = ? WHERE id = ? AND negocio_id = ?";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setString(1, e.getNome());
            ps.setString(2, e.getCategoria());
            ps.setInt(3, e.getDuracaoMinutos());
            ps.setDouble(4, e.getPreco());
            ps.setInt(5, e.getId());
            ps.setInt(6, e.getNegocioId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao atualizar servico: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM servico WHERE id = ? AND negocio_id = ?";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, negocioId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            if (String.valueOf(ex.getMessage()).contains("FOREIGN KEY")) {
                throw new ValidacaoException("Não é possível excluir: existem registros vinculados a este item.");
            }
            throw new RuntimeException("Erro ao excluir servico: " + ex.getMessage(), ex);
        }
    }

    private Servico mapear(ResultSet rs) throws SQLException {
        Servico e = new Servico();
        e.setId(rs.getInt("id"));
        e.setNegocioId(rs.getInt("negocio_id"));
        e.setNome(rs.getString("nome"));
        e.setCategoria(rs.getString("categoria"));
        e.setDuracaoMinutos(rs.getInt("duracao_minutos"));
        e.setPreco(rs.getDouble("preco"));
        return e;
    }

    private Connection conexao() {
        return ConexaoBanco.getInstancia().getConexao();
    }

    private int negocioId() {
        return GerenciadorVariabilidade.getInstancia().negocioAtualId();
    }
}
