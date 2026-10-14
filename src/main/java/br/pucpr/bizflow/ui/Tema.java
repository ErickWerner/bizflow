package br.pucpr.bizflow.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

/** Cores e componentes padronizados (front-end reutilizável). */
public final class Tema {
    public static final Color VERDE_ESCURO = new Color(0x1D4235);
    public static final Color VERDE = new Color(0x2D5F4C);
    public static final Color OCRE = new Color(0xC98A2B);
    public static final Color FUNDO = new Color(0xF6F4EE);
    public static final Color TEXTO = new Color(0x1F2A24);
    public static final Color TEXTO_SUAVE = new Color(0x5C6660);
    public static final Color VERMELHO = new Color(0xB23B3B);
    public static final Color ROXO = new Color(0x6B4C8A);
    public static final Color AZUL = new Color(0x2E6C99);

    public static final Font TITULO = new Font("SansSerif", Font.BOLD, 20);
    public static final Font NORMAL = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font NEGRITO = new Font("SansSerif", Font.BOLD, 13);
    public static final Font PEQUENA = new Font("SansSerif", Font.PLAIN, 11);

    private Tema() {}

    public static JButton botao(String texto, Color cor) {
        JButton b = new JButton(texto);
        b.setFont(NEGRITO);
        b.setForeground(Color.WHITE);
        b.setBackground(cor);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Selo colorido indicando o tipo de variabilidade da tela. */
    public static JLabel selo(String texto, Color cor) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(Color.WHITE);
        l.setOpaque(true);
        l.setBackground(cor);
        l.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        return l;
    }
}
