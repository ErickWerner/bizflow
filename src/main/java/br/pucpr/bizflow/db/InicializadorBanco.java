package br.pucpr.bizflow.db;

import br.pucpr.bizflow.variabilidade.Modulo;
import br.pucpr.bizflow.variabilidade.Segmento;
import br.pucpr.bizflow.variabilidade.TipoModulo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Cria as tabelas (se não existirem) e popula dados de exemplo na primeira execução. */
public class InicializadorBanco {

    private static final String[] DDL = {
        """
        CREATE TABLE IF NOT EXISTS negocio (
            id        INTEGER PRIMARY KEY AUTOINCREMENT,
            nome      TEXT NOT NULL,
            segmento  TEXT NOT NULL,
            cidade    TEXT
        )""",
        // Tabela da VARIABILIDADE: quais módulos opcionais/exclusivos cada negócio ligou
        """
        CREATE TABLE IF NOT EXISTS modulo_ativo (
            negocio_id INTEGER NOT NULL REFERENCES negocio(id),
            modulo     TEXT    NOT NULL,
            ativo      INTEGER NOT NULL DEFAULT 0,
            PRIMARY KEY (negocio_id, modulo)
        )""",
        """
        CREATE TABLE IF NOT EXISTS cliente (
            id          INTEGER PRIMARY KEY AUTOINCREMENT,
            negocio_id  INTEGER NOT NULL REFERENCES negocio(id),
            nome        TEXT NOT NULL,
            telefone    TEXT NOT NULL,
            email       TEXT,
            observacoes TEXT
        )""",
        """
        CREATE TABLE IF NOT EXISTS servico (
            id              INTEGER PRIMARY KEY AUTOINCREMENT,
            negocio_id      INTEGER NOT NULL REFERENCES negocio(id),
            nome            TEXT NOT NULL,
            categoria       TEXT,
            duracao_minutos INTEGER NOT NULL,
            preco           REAL NOT NULL
        )""",
        """
        CREATE TABLE IF NOT EXISTS profissional (
            id            INTEGER PRIMARY KEY AUTOINCREMENT,
            negocio_id    INTEGER NOT NULL REFERENCES negocio(id),
            nome          TEXT NOT NULL,
            especialidade TEXT NOT NULL,
            telefone      TEXT,
            ativo         INTEGER NOT NULL DEFAULT 1
        )""",
        """
        CREATE TABLE IF NOT EXISTS agendamento (
            id              INTEGER PRIMARY KEY AUTOINCREMENT,
            negocio_id      INTEGER NOT NULL REFERENCES negocio(id),
            cliente_id      INTEGER NOT NULL REFERENCES cliente(id),
            servico_id      INTEGER NOT NULL REFERENCES servico(id),
            profissional_id INTEGER NOT NULL REFERENCES profissional(id),
            data_hora       TEXT NOT NULL,
            status          TEXT NOT NULL,
            forma_pagamento TEXT NOT NULL,
            valor_final     REAL NOT NULL
        )""",
        // Tabelas de módulos EXCLUSIVOS (só usadas por certos segmentos)
        """
        CREATE TABLE IF NOT EXISTS pet (
            id            INTEGER PRIMARY KEY AUTOINCREMENT,
            negocio_id    INTEGER NOT NULL REFERENCES negocio(id),
            tutor_id      INTEGER NOT NULL REFERENCES cliente(id),
            nome          TEXT NOT NULL,
            especie       TEXT NOT NULL,
            raca          TEXT,
            ultima_vacina TEXT
        )""",
        """
        CREATE TABLE IF NOT EXISTS plano (
            id               INTEGER PRIMARY KEY AUTOINCREMENT,
            negocio_id       INTEGER NOT NULL REFERENCES negocio(id),
            nome             TEXT NOT NULL,
            valor_mensal     REAL NOT NULL,
            duracao_meses    INTEGER NOT NULL,
            aulas_por_semana INTEGER NOT NULL
        )"""
    };

    public void inicializar() {
        Connection c = ConexaoBanco.getInstancia().getConexao();
        try (Statement st = c.createStatement()) {
            for (String sql : DDL) st.execute(sql);
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM negocio")) {
                if (rs.next() && rs.getInt(1) == 0) popular(c);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar o banco: " + e.getMessage(), e);
        }
    }

    private void popular(Connection c) throws SQLException {
        int salao = negocio(c, "Salão Viva Vida", Segmento.SALAO, "Curitiba");
        int pet = negocio(c, "PetShop Amigo Fiel", Segmento.PETSHOP, "Curitiba");
        int barb = negocio(c, "Barbearia Navalha", Segmento.BARBEARIA, "Fazenda Rio Grande");
        int acad = negocio(c, "Academia Corpo em Forma", Segmento.ACADEMIA, "Curitiba");

        // Módulos: exclusivos ligados para o segmento certo, opcionais variando por cliente
        modulos(c, salao, true, true);
        modulos(c, pet, true, false);
        modulos(c, barb, false, true);
        modulos(c, acad, true, true);

        exec(c, "INSERT INTO cliente (negocio_id,nome,telefone,email,observacoes) VALUES (?,?,?,?,?)",
                salao, "Beatriz Souza", "(41) 99123-4455", "beatriz@email.com", "Prefere fim de tarde");
        exec(c, "INSERT INTO cliente (negocio_id,nome,telefone,email,observacoes) VALUES (?,?,?,?,?)",
                salao, "Marina Alves", "(41) 98877-2210", "", "");
        exec(c, "INSERT INTO cliente (negocio_id,nome,telefone,email,observacoes) VALUES (?,?,?,?,?)",
                pet, "Marcos Tadeu", "(41) 99654-1120", "marcos@email.com", "");
        exec(c, "INSERT INTO cliente (negocio_id,nome,telefone,email,observacoes) VALUES (?,?,?,?,?)",
                acad, "Rafael Nunes", "(41) 99777-3030", "rafael@email.com", "Objetivo: hipertrofia");
        exec(c, "INSERT INTO cliente (negocio_id,nome,telefone,email,observacoes) VALUES (?,?,?,?,?)",
                barb, "Diego Lima", "(41) 99888-1212", "", "");

        exec(c, "INSERT INTO servico (negocio_id,nome,categoria,duracao_minutos,preco) VALUES (?,?,?,?,?)",
                salao, "Corte + Escova", "Cabelo", 50, 80.0);
        exec(c, "INSERT INTO servico (negocio_id,nome,categoria,duracao_minutos,preco) VALUES (?,?,?,?,?)",
                salao, "Manicure", "Unhas", 40, 45.0);
        exec(c, "INSERT INTO servico (negocio_id,nome,categoria,duracao_minutos,preco) VALUES (?,?,?,?,?)",
                pet, "Banho e Tosa", "Banho", 90, 70.0);
        exec(c, "INSERT INTO servico (negocio_id,nome,categoria,duracao_minutos,preco) VALUES (?,?,?,?,?)",
                acad, "Spinning", "Aula em grupo", 50, 30.0);
        exec(c, "INSERT INTO servico (negocio_id,nome,categoria,duracao_minutos,preco) VALUES (?,?,?,?,?)",
                barb, "Corte + Barba", "Combo", 45, 55.0);

        exec(c, "INSERT INTO profissional (negocio_id,nome,especialidade,telefone,ativo) VALUES (?,?,?,?,?)",
                salao, "Fernanda Alves", "Cabeleireira", "(41) 99000-1111", 1);
        exec(c, "INSERT INTO profissional (negocio_id,nome,especialidade,telefone,ativo) VALUES (?,?,?,?,?)",
                pet, "João Tosador", "Tosa", "(41) 99000-2222", 1);
        exec(c, "INSERT INTO profissional (negocio_id,nome,especialidade,telefone,ativo) VALUES (?,?,?,?,?)",
                acad, "Carla Instrutora", "Spinning", "(41) 99000-3333", 1);
        exec(c, "INSERT INTO profissional (negocio_id,nome,especialidade,telefone,ativo) VALUES (?,?,?,?,?)",
                barb, "Navalha Silva", "Barbeiro", "(41) 99000-4444", 1);

        exec(c, "INSERT INTO pet (negocio_id,tutor_id,nome,especie,raca,ultima_vacina) VALUES (?,?,?,?,?,?)",
                pet, 3, "Thor", "Cão", "Golden", "12/06/2025");
        exec(c, "INSERT INTO plano (negocio_id,nome,valor_mensal,duracao_meses,aulas_por_semana) VALUES (?,?,?,?,?)",
                acad, "Plano Trimestral", 129.90, 3, 5);
    }

    private int negocio(Connection c, String nome, Segmento s, String cidade) throws SQLException {
        exec(c, "INSERT INTO negocio (nome,segmento,cidade) VALUES (?,?,?)", nome, s.name(), cidade);
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT last_insert_rowid()")) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private void modulos(Connection c, int negocioId, boolean relatorios, boolean whatsapp) throws SQLException {
        for (Modulo m : Modulo.values()) {
            if (m.getTipo() == TipoModulo.OBRIGATORIO) continue;
            boolean ativo = switch (m) {
                case RELATORIOS -> relatorios;
                case NOTIFICACAO_WHATSAPP -> whatsapp;
                default -> true; // exclusivos já nascem ligados (só aparecem no segmento certo)
            };
            exec(c, "INSERT INTO modulo_ativo (negocio_id,modulo,ativo) VALUES (?,?,?)",
                    negocioId, m.name(), ativo ? 1 : 0);
        }
    }

    private void exec(Connection c, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            ps.executeUpdate();
        }
    }
}
