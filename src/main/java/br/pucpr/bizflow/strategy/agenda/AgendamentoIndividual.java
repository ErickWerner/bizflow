package br.pucpr.bizflow.strategy.agenda;

import br.pucpr.bizflow.model.Agendamento;
import br.pucpr.bizflow.model.ValidacaoException;

import java.util.List;

public class AgendamentoIndividual implements RegraAgendamentoStrategy {

    @Override
    public void validar(Agendamento novo, List<Agendamento> existentes) {
        for (Agendamento a : existentes) {
            if (a.isAtivo() && a.mesmoHorario(novo) && a.getProfissionalId() == novo.getProfissionalId()) {
                throw new ValidacaoException("Este profissional já tem um atendimento em " + novo.getDataHora() + ".");
            }
        }
    }

    @Override
    public String descricao() { return "Individual: 1 atendimento por profissional por horário"; }
}
