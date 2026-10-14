package br.pucpr.bizflow.ui;

import br.pucpr.bizflow.variabilidade.GerenciadorVariabilidade;
import br.pucpr.bizflow.variabilidade.Modulo;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;

/**
 * Janela principal. O MENU NÃO É FIXO: é montado a partir de
 * GerenciadorVariabilidade.modulosVisiveisComTela(), então cada
 * combinação de negócio + perfil gera um "produto" diferente.
 */
public class MainFrame extends JFrame {
    private final JPanel menu = new JPanel();
    private final JPanel conteudo = new JPanel(new BorderLayout());
    private final Runnable trocarContexto;

    public MainFrame(Runnable trocarContexto) {
        super("BizFlow — " + GerenciadorVariabilidade.getInstancia().getNegocioAtual().getNome());
        this.trocarContexto = trocarContexto;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1180, 720);
        setLocationRelativeTo(null);

        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(Tema.VERDE_ESCURO);
        menu.setBorder(BorderFactory.createEmptyBorder(18, 12, 18, 12));
        menu.setPreferredSize(new Dimension(230, 10));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(menu, BorderLayout.WEST);
        getContentPane().add(conteudo, BorderLayout.CENTER);

        reconstruirMenu();
        mostrar(new InicioPanel());
    }

    public final void reconstruirMenu() {
        GerenciadorVariabilidade g = GerenciadorVariabilidade.getInstancia();
        menu.removeAll();

        JLabel marca = new JLabel("BizFlow");
        marca.setFont(Tema.TITULO);
        marca.setForeground(Color.WHITE);
        menu.add(alinhar(marca));
        JLabel ctx = new JLabel("<html>" + g.getNegocioAtual().getSegmento() + "<br>" + g.getUsuarioAtual() + "</html>");
        ctx.setFont(Tema.PEQUENA);
        ctx.setForeground(new Color(0xB9CFC3));
        menu.add(alinhar(ctx));
        menu.add(Box.createVerticalStrut(18));

        menu.add(itemMenu("Início", () -> mostrar(new InicioPanel()), false));
        for (Modulo m : g.modulosVisiveisComTela()) {
            menu.add(itemMenu(m.getDescricao(), () -> mostrar(criarTela(m)), m.getTipo().name().equals("EXCLUSIVO")));
        }
        menu.add(Box.createVerticalGlue());
        JButton trocar = Tema.botao("Trocar negócio / perfil", Tema.OCRE);
        trocar.addActionListener(e -> { dispose(); trocarContexto.run(); });
        menu.add(alinhar(trocar));

        menu.revalidate();
        menu.repaint();
    }

    /** "Fábrica" de telas: liga cada módulo do feature model à sua tela. */
    private JComponent criarTela(Modulo m) {
        return switch (m) {
            case CLIENTES -> new ClientePanel().construirTela();
            case SERVICOS -> new ServicoPanel().construirTela();
            case PROFISSIONAIS -> new ProfissionalPanel().construirTela();
            case AGENDAMENTOS -> new AgendamentoPanel().construirTela();
            case PETS -> new PetPanel().construirTela();
            case PLANOS -> new PlanoPanel().construirTela();
            case RELATORIOS -> new RelatorioPanel();
            case CONFIGURACAO -> new ConfiguracaoPanel(() -> { reconstruirMenu(); mostrar(new InicioPanel()); });
            default -> new JLabel("Módulo sem tela", SwingConstants.CENTER);
        };
    }

    public void mostrar(JComponent tela) {
        conteudo.removeAll();
        conteudo.add(tela, BorderLayout.CENTER);
        conteudo.revalidate();
        conteudo.repaint();
    }

    private JButton itemMenu(String texto, Runnable acao, boolean exclusivo) {
        JButton b = new JButton(exclusivo ? texto + "  ★" : texto);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFont(Tema.NORMAL);
        b.setForeground(exclusivo ? new Color(0xF5DCA8) : new Color(0xDCE8E1));
        b.setBackground(Tema.VERDE_ESCURO);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> acao.run());
        return alinhar(b);
    }

    private <C extends JComponent> C alinhar(C c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }
}
