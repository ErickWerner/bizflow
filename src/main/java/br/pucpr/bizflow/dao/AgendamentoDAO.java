package br.pucpr.bizflow.dao;

import br.pucpr.bizflow.db.ConexaoBanco;
import br.pucpr.bizflow.model.Agendamento;
import br.pucpr.bizflow.model.FormaPagamento;
import br.pucpr.bizflow.model.StatusAgendamento;
import br.pucpr.bizflow.variabilidade.GerenciadorVariabilidade;
import br.pucpr.bizflow.model.ValidacaoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** VERSÃO SEM COMPACTAÇÃO: todo o SQL e o JDBC escritos à mão neste DAO. */
public class AgendamentoDAO implements Repositorio<Agendamento> {

    @Override
    public List<Agendamento> listar() {
        List<Agendamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM agendamento WHERE negocio_id = ? ORDER BY id";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, negocioId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao listar agendamento: " + ex.getMessage(), ex);
        }
        return lista;
    }

    @Override
    public Agendamento buscarPorId(int id) {
        String sql = "SELECT * FROM agendamento WHERE id = ? AND negocio_id = ?";
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
            throw new RuntimeException("Erro ao buscar agendamento: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Agendamento salvar(Agendamento e) {
        e.validar();
        GerenciadorVariabilidade.getInstancia().regraAgendamento().validar(e, listar());
        e.setNegocioId(negocioId());
        if (e.isNovo()) {
            inserir(e);
        } else {
            atualizar(e);
        }
        return e;
    }

    private void inserir(Agendamento e) {
        String sql = "INSERT INTO agendamento (negocio_id, cliente_id, servico_id, profissional_id, data_hora, status, forma_pagamento, valor_final) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, e.getNegocioId());
            ps.setInt(2, e.getClienteId());
            ps.setInt(3, e.getServicoId());
            ps.setInt(4, e.getProfissionalId());
            ps.setString(5, e.getDataHora());
            ps.setString(6, e.getStatus().name());
            ps.setString(7, e.getFormaPagamento().name());
            ps.setDouble(8, e.getValorFinal());
            ps.executeUpdate();
            try (Statement st = conexao().createStatement();
                 ResultSet rs = st.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    e.setId(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao inserir agendamento: " + ex.getMessage(), ex);
        }
    }

    private void atualizar(Agendamento e) {
        String sql = "UPDATE agendamento SET cliente_id = ?, servico_id = ?, profissional_id = ?, data_hora = ?, status = ?, forma_pagamento = ?, valor_final = ? WHERE id = ? AND negocio_id = ?";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, e.getClienteId());
            ps.setInt(2, e.getServicoId());
            ps.setInt(3, e.getProfissionalId());
            ps.setString(4, e.getDataHora());
            ps.setString(5, e.getStatus().name());
            ps.setString(6, e.getFormaPagamento().name());
            ps.setDouble(7, e.getValorFinal());
            ps.setInt(8, e.getId());
            ps.setInt(9, e.getNegocioId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao atualizar agendamento: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM agendamento WHERE id = ? AND negocio_id = ?";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, negocioId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            if (String.valueOf(ex.getMessage()).contains("FOREIGN KEY")) {
                throw new ValidacaoException("Não é possível excluir: existem registros vinculados a este item.");
            }
            throw new RuntimeException("Erro ao excluir agendamento: " + ex.getMessage(), ex);
        }
    }

    private Agendamento mapear(ResultSet rs) throws SQLException {
        Agendamento e = new Agendamento();
        e.setId(rs.getInt("id"));
        e.setNegocioId(rs.getInt("negocio_id"));
        e.setClienteId(rs.getInt("cliente_id"));
        e.setServicoId(rs.getInt("servico_id"));
        e.setProfissionalId(rs.getInt("profissional_id"));
        e.setDataHora(rs.getString("data_hora"));
        e.setStatus(StatusAgendamento.valueOf(rs.getString("status")));
        e.setFormaPagamento(FormaPagamento.valueOf(rs.getString("forma_pagamento")));
        e.setValorFinal(rs.getDouble("valor_final"));
        return e;
    }

    private Connection conexao() {
        return ConexaoBanco.getInstancia().getConexao();
    }

    private int negocioId() {
        return GerenciadorVariabilidade.getInstancia().negocioAtualId();
    }
}
