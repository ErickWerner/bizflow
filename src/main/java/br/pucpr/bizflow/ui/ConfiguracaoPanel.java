package br.pucpr.bizflow.ui;

import br.pucpr.bizflow.model.Negocio;
import br.pucpr.bizflow.variabilidade.GerenciadorVariabilidade;
import br.pucpr.bizflow.variabilidade.Modulo;
import br.pucpr.bizflow.variabilidade.Perfil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.stream.Collectors;

/**
 * EXEMPLO DE COMO A VARIABILIDADE FOI PLANEJADA.
 * Mostra o feature model inteiro e deixa o Dono ligar/desligar módulos
 * configuráveis. A mudança é gravada no banco (tabela modulo_ativo)
 * e o menu é reconstruído na hora, sem recompilar nada.
 */
public class ConfiguracaoPanel extends JPanel {
    private final Runnable aoAlterar;
    private DefaultTableModel modelo;

    public ConfiguracaoPanel(Runnable aoAlterar) {
        this.aoAlterar = aoAlterar;
        setLayout(new BorderLayout(0, 12));
        setBackground(Tema.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        GerenciadorVariabilidade g = GerenciadorVariabilidade.getInstancia();
        Negocio n = g.getNegocioAtual();

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        JLabel t = new JLabel("Configuração da Linha de Produto — " + n.getNome());
        t.setFont(Tema.TITULO);
        t.setForeground(Tema.VERDE_ESCURO);
        JLabel d = new JLabel("<html>Segmento: <b>" + n.getSegmento() + "</b>. "
                + "Marque/desmarque a coluna <b>Ativo</b> nos módulos configuráveis e clique em Aplicar. "
                + "Módulos de outros segmentos aparecem como \"não existe\" — é a variabilidade por cliente.</html>");
        d.setFont(Tema.NORMAL);
        topo.add(t, BorderLayout.NORTH);
        topo.add(d, BorderLayout.SOUTH);

        modelo = new DefaultTableModel(new String[]{"Módulo", "Tipo", "Existe neste segmento?", "Perfis com acesso", "Ativo"}, 0) {
            @Override public Class<?> getColumnClass(int c) { return c == 4 ? Boolean.class : String.class; }

            @Override public boolean isCellEditable(int r, int c) {
                Modulo m = Modulo.values()[r];
                return c == 4 && m.isConfiguravel() && m.existeNoSegmento(n.getSegmento());
            }
        };
        for (Modulo m : Modulo.values()) {
            String perfis = m.getPerfis().stream().map(Perfil::name).collect(Collectors.joining(", "));
            modelo.addRow(new Object[]{m.getDescricao(), m.getTipo().name(),
                    m.existeNoSegmento(n.getSegmento()) ? "Sim" : "Não existe",
                    perfis, g.isDisponivel(m)});
        }
        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(26);
        tabela.setFont(Tema.NORMAL);
        tabela.getTableHeader().setFont(Tema.NEGRITO);

        JButton aplicar = Tema.botao("Aplicar configuração", Tema.VERDE);
        aplicar.addActionListener(e -> aplicar());
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rodape.setOpaque(false);
        rodape.add(aplicar);

        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private void aplicar() {
        GerenciadorVariabilidade g = GerenciadorVariabilidade.getInstancia();
        for (int i = 0; i < Modulo.values().length; i++) {
            Modulo m = Modulo.values()[i];
            if (m.isConfiguravel() && m.existeNoSegmento(g.getNegocioAtual().getSegmento())) {
                g.definirAtivo(m, (Boolean) modelo.getValueAt(i, 4));
            }
        }
        aoAlterar.run();
    }
}
