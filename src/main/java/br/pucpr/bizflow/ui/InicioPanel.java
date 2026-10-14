package br.pucpr.bizflow.ui;

import br.pucpr.bizflow.model.Negocio;
import br.pucpr.bizflow.variabilidade.GerenciadorVariabilidade;
import br.pucpr.bizflow.variabilidade.Modulo;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/** Tela inicial: mostra o que este cliente/usuário enxerga e por quê. */
public class InicioPanel extends JPanel {

    public InicioPanel() {
        setLayout(new BorderLayout());
        setBackground(Tema.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        GerenciadorVariabilidade g = GerenciadorVariabilidade.getInstancia();
        Negocio n = g.getNegocioAtual();

        StringBuilder html = new StringBuilder("<html><div style='font-family:sans-serif;font-size:12px;width:640px'>");
        html.append("<h1 style='color:#1D4235'>").append(n.getNome()).append("</h1>");
        html.append("<p>Segmento: <b>").append(n.getSegmento()).append("</b> &nbsp;|&nbsp; Usuário: <b>")
            .append(g.getUsuarioAtual()).append("</b></p>");
        html.append("<p>Regra de agenda (Strategy): <b>").append(g.regraAgendamento().descricao()).append("</b></p>");
        html.append("<h3 style='color:#2D5F4C'>Módulos desta instância do produto</h3><table cellpadding='4'>");
        for (Modulo m : Modulo.values()) {
            String situacao;
            String cor;
            if (g.isVisivel(m)) { situacao = m.isTemTela() ? "VISÍVEL" : "ATIVO"; cor = "#2D5F4C"; }
            else if (!m.existeNoSegmento(n.getSegmento())) { situacao = "não existe neste segmento"; cor = "#999999"; }
            else if (!g.isDisponivel(m)) { situacao = "desligado para este cliente"; cor = "#C98A2B"; }
            else { situacao = "oculto para o perfil " + g.getUsuarioAtual().getPerfil(); cor = "#2E6C99"; }
            html.append("<tr><td>").append(m.getDescricao()).append("</td><td>").append(m.getTipo())
                .append("</td><td style='color:").append(cor).append("'><b>").append(situacao).append("</b></td></tr>");
        }
        html.append("</table><p style='color:#5C6660'>Use o botão \"Trocar negócio / perfil\" (embaixo, no menu) e veja o menu mudar.</p></div></html>");
        add(new JLabel(html.toString()), BorderLayout.NORTH);
    }
}
