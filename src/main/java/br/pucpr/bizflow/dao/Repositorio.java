package br.pucpr.bizflow.dao;

import java.util.List;

/** Contrato de CRUD usado pelas telas — as telas não sabem nada de SQL. */
public interface Repositorio<T> {
    List<T> listar();

    T buscarPorId(int id);

    T salvar(T entidade);   // insere se for novo, atualiza se já existir

    void excluir(int id);
}
