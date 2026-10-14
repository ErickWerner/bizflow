package br.pucpr.bizflow.dao;

import br.pucpr.bizflow.db.ConexaoBanco;
import br.pucpr.bizflow.model.Profissional;
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
public class ProfissionalDAO implements Repositorio<Profissional> {

    @Override
    public List<Profissional> listar() {
        List<Profissional> lista = new ArrayList<>();
        String sql = "SELECT * FROM profissional WHERE negocio_id = ? ORDER BY nome";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, negocioId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao listar profissional: " + ex.getMessage(), ex);
        }
        return lista;
    }

    @Override
    public Profissional buscarPorId(int id) {
        String sql = "SELECT * FROM profissional WHERE id = ? AND negocio_id = ?";
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
            throw new RuntimeException("Erro ao buscar profissional: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Profissional salvar(Profissional e) {
        e.validar();
        e.setNegocioId(negocioId());
        if (e.isNovo()) {
            inserir(e);
        } else {
            atualizar(e);
        }
        return e;
    }

    private void inserir(Profissional e) {
        String sql = "INSERT INTO profissional (negocio_id, nome, especialidade, telefone, ativo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, e.getNegocioId());
            ps.setString(2, e.getNome());
            ps.setString(3, e.getEspecialidade());
            ps.setString(4, e.getTelefone());
            ps.setInt(5, e.isAtivo() ? 1 : 0);
            ps.executeUpdate();
            try (Statement st = conexao().createStatement();
                 ResultSet rs = st.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    e.setId(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao inserir profissional: " + ex.getMessage(), ex);
        }
    }

    private void atualizar(Profissional e) {
        String sql = "UPDATE profissional SET nome = ?, especialidade = ?, telefone = ?, ativo = ? WHERE id = ? AND negocio_id = ?";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setString(1, e.getNome());
            ps.setString(2, e.getEspecialidade());
            ps.setString(3, e.getTelefone());
            ps.setInt(4, e.isAtivo() ? 1 : 0);
            ps.setInt(5, e.getId());
            ps.setInt(6, e.getNegocioId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao atualizar profissional: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM profissional WHERE id = ? AND negocio_id = ?";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, negocioId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            if (String.valueOf(ex.getMessage()).contains("FOREIGN KEY")) {
                throw new ValidacaoException("Não é possível excluir: existem registros vinculados a este item.");
            }
            throw new RuntimeException("Erro ao excluir profissional: " + ex.getMessage(), ex);
        }
    }

    private Profissional mapear(ResultSet rs) throws SQLException {
        Profissional e = new Profissional();
        e.setId(rs.getInt("id"));
        e.setNegocioId(rs.getInt("negocio_id"));
        e.setNome(rs.getString("nome"));
        e.setEspecialidade(rs.getString("especialidade"));
        e.setTelefone(rs.getString("telefone"));
        e.setAtivo(rs.getInt("ativo") == 1);
        return e;
    }

    private Connection conexao() {
        return ConexaoBanco.getInstancia().getConexao();
    }

    private int negocioId() {
        return GerenciadorVariabilidade.getInstancia().negocioAtualId();
    }
}
