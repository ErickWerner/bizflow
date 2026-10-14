package br.pucpr.bizflow.strategy.agenda;

import br.pucpr.bizflow.model.Agendamento;
import br.pucpr.bizflow.model.ValidacaoException;

import java.util.List;

public class AgendamentoEmGrupo implements RegraAgendamentoStrategy {
    private final int capacidade;

    public AgendamentoEmGrupo(int capacidade) { this.capacidade = capacidade; }

    @Override
    public void validar(Agendamento novo, List<Agendamento> existentes) {
        long ocupadas = existentes.stream()
                .filter(Agendamento::isAtivo)
                .filter(a -> a.mesmoHorario(novo) && a.getServicoId() == novo.getServicoId())
                .count();
        if (ocupadas >= capacidade) {
            throw new ValidacaoException("Turma lotada: " + capacidade + " vagas já ocupadas nesse horário.");
        }
    }

    @Override
    public String descricao() { return "Em grupo: até " + capacidade + " pessoas por horário"; }
}
