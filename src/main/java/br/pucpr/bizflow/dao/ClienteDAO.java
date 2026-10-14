package br.pucpr.bizflow.dao;

import br.pucpr.bizflow.db.ConexaoBanco;
import br.pucpr.bizflow.model.Cliente;
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
public class ClienteDAO implements Repositorio<Cliente> {

    @Override
    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE negocio_id = ? ORDER BY nome";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, negocioId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao listar cliente: " + ex.getMessage(), ex);
        }
        return lista;
    }

    @Override
    public Cliente buscarPorId(int id) {
        String sql = "SELECT * FROM cliente WHERE id = ? AND negocio_id = ?";
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
            throw new RuntimeException("Erro ao buscar cliente: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Cliente salvar(Cliente e) {
        e.validar();
        e.setNegocioId(negocioId());
        if (e.isNovo()) {
            inserir(e);
        } else {
            atualizar(e);
        }
        return e;
    }

    private void inserir(Cliente e) {
        String sql = "INSERT INTO cliente (negocio_id, nome, telefone, email, observacoes) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, e.getNegocioId());
            ps.setString(2, e.getNome());
            ps.setString(3, e.getTelefone());
            ps.setString(4, e.getEmail());
            ps.setString(5, e.getObservacoes());
            ps.executeUpdate();
            try (Statement st = conexao().createStatement();
                 ResultSet rs = st.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    e.setId(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao inserir cliente: " + ex.getMessage(), ex);
        }
    }

    private void atualizar(Cliente e) {
        String sql = "UPDATE cliente SET nome = ?, telefone = ?, email = ?, observacoes = ? WHERE id = ? AND negocio_id = ?";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setString(1, e.getNome());
            ps.setString(2, e.getTelefone());
            ps.setString(3, e.getEmail());
            ps.setString(4, e.getObservacoes());
            ps.setInt(5, e.getId());
            ps.setInt(6, e.getNegocioId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao atualizar cliente: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM cliente WHERE id = ? AND negocio_id = ?";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, negocioId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            if (String.valueOf(ex.getMessage()).contains("FOREIGN KEY")) {
                throw new ValidacaoException("Não é possível excluir: existem registros vinculados a este item.");
            }
            throw new RuntimeException("Erro ao excluir cliente: " + ex.getMessage(), ex);
        }
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente e = new Cliente();
        e.setId(rs.getInt("id"));
        e.setNegocioId(rs.getInt("negocio_id"));
        e.setNome(rs.getString("nome"));
        e.setTelefone(rs.getString("telefone"));
        e.setEmail(rs.getString("email"));
        e.setObservacoes(rs.getString("observacoes"));
        return e;
    }

    private Connection conexao() {
        return ConexaoBanco.getInstancia().getConexao();
    }

    private int negocioId() {
        return GerenciadorVariabilidade.getInstancia().negocioAtualId();
    }
}
