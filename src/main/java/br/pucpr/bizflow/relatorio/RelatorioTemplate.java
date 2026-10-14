package br.pucpr.bizflow.relatorio;

import br.pucpr.bizflow.model.Negocio;
import br.pucpr.bizflow.variabilidade.GerenciadorVariabilidade;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TEMPLATE METHOD #3 — todo relatório tem o mesmo formato:
 * cabeçalho -> corpo -> (resumo opcional) -> rodapé.
 * O gerar() é final; cada relatório concreto só escreve o corpo.
 */
public abstract class RelatorioTemplate {

    public final String gerar() {
        StringBuilder sb = new StringBuilder();
        cabecalho(sb);
        corpo(sb);
        if (incluirResumo()) {
            sb.append("\n--- RESUMO ---\n");
            resumo(sb);
        }
        rodape(sb);
        return sb.toString();
    }

    // passos abstratos
    public abstract String titulo();

    protected abstract void corpo(StringBuilder sb);

    // ganchos com comportamento padrão
    protected boolean incluirResumo() { return true; }

    protected void resumo(StringBuilder sb) {}

    // passos fixos (iguais para todos os relatórios = reuso)
    private void cabecalho(StringBuilder sb) {
        GerenciadorVariabilidade g = GerenciadorVariabilidade.getInstancia();
        Negocio n = g.getNegocioAtual();
        sb.append("==============================================\n");
        sb.append(" BizFlow | ").append(titulo().toUpperCase()).append('\n');
        sb.append(" ").append(n.getNome()).append(" — ").append(n.getSegmento().getDescricao()).append('\n');
        sb.append(" Emitido por: ").append(g.getUsuarioAtual()).append('\n');
        sb.append("==============================================\n\n");
    }

    private void rodape(StringBuilder sb) {
        sb.append("\n----------------------------------------------\n");
        sb.append("Gerado em ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        sb.append('\n');
    }
}
