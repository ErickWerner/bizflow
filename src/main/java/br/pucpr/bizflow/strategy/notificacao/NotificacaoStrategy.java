package br.pucpr.bizflow.strategy.notificacao;

import br.pucpr.bizflow.model.Cliente;

/** STRATEGY #3 — por qual canal o cliente é avisado do agendamento. */
public interface NotificacaoStrategy {
    /** Retorna o texto "enviado" (simulado — não há integração real). */
    String enviar(Cliente cliente, String mensagem);

    String canal();
}
