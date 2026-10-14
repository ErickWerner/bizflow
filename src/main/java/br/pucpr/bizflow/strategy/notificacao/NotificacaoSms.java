package br.pucpr.bizflow.strategy.notificacao;

import br.pucpr.bizflow.model.Cliente;

public class NotificacaoSms implements NotificacaoStrategy {
    private static final int LIMITE = 160;

    @Override
    public String enviar(Cliente cliente, String mensagem) {
        String texto = mensagem.length() > LIMITE ? mensagem.substring(0, LIMITE - 3) + "..." : mensagem;
        return "[SMS -> " + cliente.telefoneNormalizado() + "] " + texto;
    }

    @Override
    public String canal() { return "SMS"; }
}
