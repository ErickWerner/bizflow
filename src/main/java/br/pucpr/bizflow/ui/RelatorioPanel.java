package br.pucpr.bizflow.ui;

import br.pucpr.bizflow.relatorio.RelatorioAgendamentos;
import br.pucpr.bizflow.relatorio.RelatorioClientes;
import br.pucpr.bizflow.relatorio.RelatorioFaturamento;
import br.pucpr.bizflow.relatorio.RelatorioTemplate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.DefaultListCellRenderer;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

/** Módulo OPCIONAL: usa os 3 relatórios do Template Method #3. */
public class RelatorioPanel extends JPanel {

    public RelatorioPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(Tema.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topo.setOpaque(false);
        JLabel t = new JLabel("Relatórios");
        t.setFont(Tema.TITULO);
        t.setForeground(Tema.VERDE_ESCURO);
        topo.add(t);
        topo.add(Tema.selo("OPCIONAL", Tema.AZUL));

        RelatorioTemplate[] relatorios = {new RelatorioClientes(), new RelatorioAgendamentos(), new RelatorioFaturamento()};
        JComboBox<RelatorioTemplate> combo = new JComboBox<>(relatorios);
        combo.setRenderer(rendererPorTitulo());
        JButton gerar = Tema.botao("Gerar", Tema.VERDE);
        topo.add(combo);
        topo.add(gerar);

        JTextArea saida = new JTextArea();
        saida.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        saida.setEditable(false);
        gerar.addActionListener(e -> saida.setText(((RelatorioTemplate) combo.getSelectedItem()).gerar()));

        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(saida), BorderLayout.CENTER);
        saida.setText(relatorios[0].gerar());
    }

    private ListCellRenderer<Object> rendererPorTitulo() {
        return new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> l, Object v, int i, boolean s, boolean f) {
                return super.getListCellRendererComponent(l, v instanceof RelatorioTemplate r ? r.titulo() : v, i, s, f);
            }
        };
    }
}
