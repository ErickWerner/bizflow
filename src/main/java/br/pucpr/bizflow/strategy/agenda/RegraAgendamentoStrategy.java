package br.pucpr.bizflow.strategy.agenda;

import br.pucpr.bizflow.model.Agendamento;

import java.util.List;

/**
 * STRATEGY #1 — regra de ocupação do horário.
 * Salão/petshop/barbearia: 1 cliente por profissional por horário.
 * Academia: vários alunos no mesmo horário (aula em grupo), até a capacidade.
 */
public interface RegraAgendamentoStrategy {
    /** Lança ValidacaoException se o novo agendamento não puder ser marcado. */
    void validar(Agendamento novo, List<Agendamento> existentes);

    String descricao();
}
