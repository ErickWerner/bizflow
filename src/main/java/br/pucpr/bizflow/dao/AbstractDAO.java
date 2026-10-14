package br.pucpr.bizflow.dao;

import br.pucpr.bizflow.db.ConexaoBanco;
import br.pucpr.bizflow.model.Entidade;
import br.pucpr.bizflow.model.ValidacaoException;
import br.pucpr.bizflow.variabilidade.GerenciadorVariabilidade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * TEMPLATE METHOD #1 — o "esqueleto" do CRUD fica aqui, uma vez só.
 *
 * O método salvar() é final: define a ORDEM dos passos
 * (validar -> marcar o negócio -> inserir/atualizar -> pós-salvar).
 * As subclasses só preenchem as lacunas: nome da tabela, colunas,
 * valores e como converter uma linha do banco em objeto.
 *
 * Também é a técnica de COMPACTAÇÃO do projeto: todo o SQL de
 * INSERT/UPDATE/DELETE/SELECT é gerado aqui, então cada DAO concreto
 * tem ~20 linhas em vez de ~100.
 */
public abstract class AbstractDAO<T extends Entidade> implements Repositorio<T> {

    // ---------- passos que a subclasse É OBRIGADA a implementar ----------
    protected abstract String tabela();

    /** Colunas persistidas, sem "id" e sem "negocio_id" (esses a base cuida). */
    protected abstract String[] colunas();

    /** Valores na MESMA ordem de colunas(). */
    protected abstract Object[] valores(T entidade);

    /** Converte a linha atual do ResultSet em objeto. */
    protected abstract T mapear(ResultSet rs) throws SQLException;

    // ---------- ganchos (hooks) que a subclasse PODE sobrescrever ----------
    protected void validar(T entidade) { entidade.validar(); }

    protected void depoisDeSalvar(T entidade) {}

    protected String ordenacao() { return "id"; }

    // ---------- métodos template (fluxo fixo) ----------
    @Override
    public final T salvar(T entidade) {
        validar(entidade);
        entidade.setNegocioId(negocioId());
        if (entidade.isNovo()) {
            inserir(entidade);
        } else {
            atualizar(entidade);
        }
        depoisDeSalvar(entidade);
        return entidade;
    }

    @Override
    public List<T> listar() {
        String sql = "SELECT * FROM " + tabela() + " WHERE negocio_id = ? ORDER BY " + ordenacao();
        List<T> lista = new ArrayList<>();
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, negocioId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearComBase(rs));
            }
        } catch (SQLException e) {
            throw erro("listar", e);
        }
        return lista;
    }

    @Override
    public T buscarPorId(int id) {
        String sql = "SELECT * FROM " + tabela() + " WHERE id = ? AND negocio_id = ?";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, negocioId());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapearComBase(rs) : null;
            }
        } catch (SQLException e) {
            throw erro("buscar", e);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM " + tabela() + " WHERE id = ? AND negocio_id = ?";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, negocioId());
            ps.executeUpdate();
        } catch (SQLException e) {
            if (String.valueOf(e.getMessage()).contains("FOREIGN KEY")) {
                throw new ValidacaoException("Não é possível excluir: existem registros vinculados a este item.");
            }
            throw erro("excluir", e);
        }
    }

    // ---------- SQL gerado automaticamente a partir de colunas() ----------
    private void inserir(T e) {
        String[] cols = colunas();
        String sql = "INSERT INTO " + tabela() + " (negocio_id, " + String.join(", ", cols) + ") VALUES (?"
                + ", ?".repeat(cols.length) + ")";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            ps.setInt(1, e.getNegocioId());
            preencher(ps, valores(e), 2);
            ps.executeUpdate();
            try (Statement st = conexao().createStatement();
                 ResultSet rs = st.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) e.setId(rs.getInt(1));
            }
        } catch (SQLException ex) {
            throw erro("inserir", ex);
        }
    }

    private void atualizar(T e) {
        String[] cols = colunas();
        String sql = "UPDATE " + tabela() + " SET " + String.join(" = ?, ", cols) + " = ? WHERE id = ? AND negocio_id = ?";
        try (PreparedStatement ps = conexao().prepareStatement(sql)) {
            int prox = preencher(ps, valores(e), 1);
            ps.setInt(prox, e.getId());
            ps.setInt(prox + 1, e.getNegocioId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw erro("atualizar", ex);
        }
    }

    private int preencher(PreparedStatement ps, Object[] vals, int inicio) throws SQLException {
        int i = inicio;
        for (Object v : vals) {
            if (v instanceof Enum<?> en) v = en.name();
            if (v instanceof Boolean b) v = b ? 1 : 0;
            ps.setObject(i++, v);
        }
        return i;
    }

    private T mapearComBase(ResultSet rs) throws SQLException {
        T e = mapear(rs);
        e.setId(rs.getInt("id"));
        e.setNegocioId(rs.getInt("negocio_id"));
        return e;
    }

    protected Connection conexao() { return ConexaoBanco.getInstancia().getConexao(); }

    protected int negocioId() { return GerenciadorVariabilidade.getInstancia().negocioAtualId(); }

    private RuntimeException erro(String operacao, SQLException e) {
        return new RuntimeException("Erro ao " + operacao + " em " + tabela() + ": " + e.getMessage(), e);
    }
}
