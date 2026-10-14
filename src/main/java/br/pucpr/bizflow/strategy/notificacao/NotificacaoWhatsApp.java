package br.pucpr.bizflow.strategy.notificacao;

import br.pucpr.bizflow.model.Cliente;

public class NotificacaoWhatsApp implements NotificacaoStrategy {
    @Override
    public String enviar(Cliente cliente, String mensagem) {
        return "[WhatsApp -> +55" + cliente.telefoneNormalizado() + "] " + mensagem;
    }

    @Override
    public String canal() { return "WhatsApp"; }
}
