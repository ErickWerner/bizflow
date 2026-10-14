package br.pucpr.bizflow.strategy.notificacao;

import br.pucpr.bizflow.model.Cliente;

public class NotificacaoEmail implements NotificacaoStrategy {
    @Override
    public String enviar(Cliente cliente, String mensagem) {
        return "[E-mail -> " + cliente.getEmail() + "] Assunto: Seu agendamento | " + mensagem;
    }

    @Override
    public String canal() { return "E-mail"; }
}
