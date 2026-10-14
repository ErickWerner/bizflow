package br.pucpr.bizflow.variabilidade;

/**
 * Classificação de features da Linha de Produto:
 *  OBRIGATORIO -> todo produto gerado tem (núcleo comum)
 *  OPCIONAL    -> qualquer cliente pode ligar/desligar
 *  EXCLUSIVO   -> só existe para um segmento específico
 */
public enum TipoModulo {
    OBRIGATORIO, OPCIONAL, EXCLUSIVO
}
