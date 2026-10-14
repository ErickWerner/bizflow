package br.pucpr.bizflow.model;

/** Erro de regra de negócio, exibido ao usuário na tela. */
public class ValidacaoException extends RuntimeException {
    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}
