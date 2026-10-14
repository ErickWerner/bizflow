package br.pucpr.bizflow.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SINGLETON #1 — uma única conexão com o banco SQLite para toda a aplicação.
 *
 * Por que Singleton? Abrir conexão é caro e o SQLite trabalha melhor com
 * uma conexão só (evita "database is locked"). Construtor privado +
 * método estático getInstancia() garantem que só exista UM objeto.
 */
public final class ConexaoBanco {

    private static ConexaoBanco instancia;

    private final String url;
    private Connection conexao;

    private ConexaoBanco() {
        // Permite trocar o arquivo do banco por parâmetro (útil para testes)
        String arquivo = System.getProperty("bizflow.db", "bizflow.db");
        this.url = "jdbc:sqlite:" + arquivo;
    }

    public static synchronized ConexaoBanco getInstancia() {
        if (instancia == null) {
            instancia = new ConexaoBanco();
        }
        return instancia;
    }

    public synchronized Connection getConexao() {
        try {
            if (conexao == null || conexao.isClosed()) {
                conexao = DriverManager.getConnection(url);
                try (Statement st = conexao.createStatement()) {
                    st.execute("PRAGMA foreign_keys = ON");
                }
            }
            return conexao;
        } catch (SQLException e) {
            throw new RuntimeException("Não foi possível conectar ao banco: " + e.getMessage(), e);
        }
    }

    public synchronized void fechar() {
        try {
            if (conexao != null) conexao.close();
        } catch (SQLException ignored) {
        }
    }
}
