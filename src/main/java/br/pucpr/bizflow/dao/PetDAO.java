package br.pucpr.bizflow.dao;

import br.pucpr.bizflow.db.ConexaoBanco;
import br.pucpr.bizflow.model.Pet;
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
public class PetDAO implements Repositorio<Pet> {

    @Override
    public List<Pet> listar() {
        List<Pet> lista = new ArrayList<>();
        String sql = "SELECT * FROM pet WHERE negocio_id = ? ORDER BY id";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, negocioId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao listar pet: " + ex.getMessage(), ex);
        }
        return lista;
    }

    @Override
    public Pet buscarPorId(int id) {
        String sql = "SELECT * FROM pet WHERE id = ? AND negocio_id = ?";
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
            throw new RuntimeException("Erro ao buscar pet: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Pet salvar(Pet e) {
        e.validar();
        e.setNegocioId(negocioId());
        if (e.isNovo()) {
            inserir(e);
        } else {
            atualizar(e);
        }
        return e;
    }

    private void inserir(Pet e) {
        String sql = "INSERT INTO pet (negocio_id, tutor_id, nome, especie, raca, ultima_vacina) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, e.getNegocioId());
            ps.setInt(2, e.getTutorId());
            ps.setString(3, e.getNome());
            ps.setString(4, e.getEspecie());
            ps.setString(5, e.getRaca());
            ps.setString(6, e.getUltimaVacina());
            ps.executeUpdate();
            try (Statement st = conexao().createStatement();
                 ResultSet rs = st.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    e.setId(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao inserir pet: " + ex.getMessage(), ex);
        }
    }

    private void atualizar(Pet e) {
        String sql = "UPDATE pet SET tutor_id = ?, nome = ?, especie = ?, raca = ?, ultima_vacina = ? WHERE id = ? AND negocio_id = ?";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, e.getTutorId());
            ps.setString(2, e.getNome());
            ps.setString(3, e.getEspecie());
            ps.setString(4, e.getRaca());
            ps.setString(5, e.getUltimaVacina());
            ps.setInt(6, e.getId());
            ps.setInt(7, e.getNegocioId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao atualizar pet: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM pet WHERE id = ? AND negocio_id = ?";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, negocioId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            if (String.valueOf(ex.getMessage()).contains("FOREIGN KEY")) {
                throw new ValidacaoException("Não é possível excluir: existem registros vinculados a este item.");
            }
            throw new RuntimeException("Erro ao excluir pet: " + ex.getMessage(), ex);
        }
    }

    private Pet mapear(ResultSet rs) throws SQLException {
        Pet e = new Pet();
        e.setId(rs.getInt("id"));
        e.setNegocioId(rs.getInt("negocio_id"));
        e.setTutorId(rs.getInt("tutor_id"));
        e.setNome(rs.getString("nome"));
        e.setEspecie(rs.getString("especie"));
        e.setRaca(rs.getString("raca"));
        e.setUltimaVacina(rs.getString("ultima_vacina"));
        return e;
    }

    private Connection conexao() {
        return ConexaoBanco.getInstancia().getConexao();
    }

    private int negocioId() {
        return GerenciadorVariabilidade.getInstancia().negocioAtualId();
    }
}
