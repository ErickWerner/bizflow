package br.pucpr.bizflow;

import br.pucpr.bizflow.db.InicializadorBanco;
import br.pucpr.bizflow.ui.LoginDialog;
import br.pucpr.bizflow.ui.MainFrame;
import br.pucpr.bizflow.variabilidade.GerenciadorVariabilidade;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        new InicializadorBanco().inicializar();
        SwingUtilities.invokeLater(Main::entrar);
    }

    /** Login -> carrega configuração do negócio -> abre a janela com o menu daquele "produto". */
    private static void entrar() {
        LoginDialog login = new LoginDialog();
        login.setVisible(true);
        if (!login.isConfirmado()) {
            System.exit(0);
        }
        GerenciadorVariabilidade.getInstancia().iniciarSessao(login.negocioEscolhido(), login.usuarioEscolhido());
        new MainFrame(Main::entrar).setVisible(true);
    }
}
