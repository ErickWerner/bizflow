package br.pucpr.bizflow.ui;

import br.pucpr.bizflow.dao.NegocioDAO;
import br.pucpr.bizflow.model.Negocio;
import br.pucpr.bizflow.model.Usuario;
import br.pucpr.bizflow.variabilidade.Perfil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridLayout;

/** Escolha do negócio (tenant) e do perfil — os dois eixos de variabilidade. */
public class LoginDialog extends JDialog {
    private final JComboBox<Negocio> negocio = new JComboBox<>();
    private final JComboBox<Perfil> perfil = new JComboBox<>(Perfil.values());
    private final JTextField nome = new JTextField("Fernanda");
    private boolean confirmado;

    public LoginDialog() {
        super((java.awt.Frame) null, "BizFlow — Entrar", true);
        for (Negocio n : new NegocioDAO().listar()) negocio.addItem(n);

        JPanel p = new JPanel(new GridLayout(0, 1, 4, 4));
        p.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        p.setBackground(Tema.FUNDO);
        JLabel t = new JLabel("BizFlow — Linha de Produto");
        t.setFont(Tema.TITULO);
        t.setForeground(Tema.VERDE_ESCURO);
        p.add(t);
        p.add(new JLabel("Negócio (cliente do BizFlow):"));
        p.add(negocio);
        p.add(new JLabel("Perfil de acesso:"));
        p.add(perfil);
        p.add(new JLabel("Seu nome:"));
        p.add(nome);
        JButton entrar = Tema.botao("Entrar", Tema.VERDE);
        entrar.addActionListener(e -> { confirmado = true; dispose(); });
        p.add(entrar);
        getRootPane().setDefaultButton(entrar);

        setContentPane(p);
        pack();
        setSize(380, getHeight());
        setLocationRelativeTo(null);
    }

    public boolean isConfirmado() { return confirmado; }

    public Negocio negocioEscolhido() { return (Negocio) negocio.getSelectedItem(); }

    public Usuario usuarioEscolhido() {
        String n = nome.getText().isBlank() ? "Usuário" : nome.getText().trim();
        return new Usuario(n, (Perfil) perfil.getSelectedItem());
    }
}
