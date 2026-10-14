package br.pucpr.bizflow.ui;

import br.pucpr.bizflow.dao.Repositorio;
import br.pucpr.bizflow.model.Entidade;
import br.pucpr.bizflow.model.ValidacaoException;
import br.pucpr.bizflow.variabilidade.Modulo;
import br.pucpr.bizflow.variabilidade.TipoModulo;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

/**
 * TEMPLATE METHOD #2 — esqueleto de TODA tela de CRUD.
 *
 * construirTela() é final e monta sempre a mesma estrutura:
 * cabeçalho -> formulário -> botões -> tabela -> carregar dados.
 * Cada tela concreta só diz: quais campos, quais colunas, como ler/preencher
 * o formulário e qual repositório usar. Novas telas = ~60 linhas.
 */
public abstract class AbstractCrudPanel<T extends Entidade> extends JPanel {

    private final JPanel formulario = new JPanel(new GridBagLayout());
    private int linhaFormulario = 0;
    private DefaultTableModel modeloTabela;
    private JTable tabela;
    private final JLabel status = new JLabel(" ");
    private List<T> itens = new ArrayList<>();
    private T selecionado;

    // ---------------- passos obrigatórios ----------------
    protected abstract String titulo();

    protected abstract String descricao();

    protected abstract Modulo modulo();

    protected abstract String[] colunas();

    protected abstract Object[] linha(T item);

    /** Chama adicionarCampo(...) para cada campo do formulário. */
    protected abstract void criarCampos();

    protected abstract void preencherFormulario(T item);

    /** Lê os campos e devolve um objeto NOVO (a base cuida do id). */
    protected abstract T lerFormulario();

    protected abstract void limparCampos();

    protected abstract Repositorio<T> repositorio();

    // ---------------- ganchos opcionais ----------------
    /** Recarregar combos dependentes (ex.: lista de clientes no agendamento). */
    protected void aoAtualizar() {}

    protected void aposSalvar(T item) {}

    // ---------------- MÉTODO TEMPLATE ----------------
    public final AbstractCrudPanel<T> construirTela() {
        setLayout(new BorderLayout(0, 12));
        setBackground(Tema.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        add(montarCabecalho(), BorderLayout.NORTH);

        criarCampos();
        JPanel esquerda = new JPanel(new BorderLayout(0, 10));
        esquerda.setOpaque(false);
        formulario.setBackground(Color.WHITE);
        formulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE4E0D5)),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        esquerda.add(formulario, BorderLayout.NORTH);
        esquerda.add(montarBotoes(), BorderLayout.CENTER);
        esquerda.setPreferredSize(new Dimension(360, 10));

        add(esquerda, BorderLayout.WEST);
        add(montarTabela(), BorderLayout.CENTER);

        status.setFont(Tema.PEQUENA);
        status.setForeground(Tema.TEXTO_SUAVE);
        add(status, BorderLayout.SOUTH);

        atualizar();
        return this;
    }

    public final void atualizar() {
        aoAtualizar();
        itens = repositorio().listar();
        modeloTabela.setRowCount(0);
        for (T item : itens) modeloTabela.addRow(linha(item));
        selecionado = null;
        status.setText(itens.size() + " registro(s) — dados lidos do banco SQLite");
    }

    // ---------------- partes fixas reutilizadas por todas as telas ----------------
    private JComponent montarCabecalho() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JPanel linha1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linha1.setOpaque(false);
        JLabel t = new JLabel(titulo());
        t.setFont(Tema.TITULO);
        t.setForeground(Tema.VERDE_ESCURO);
        linha1.add(t);
        linha1.add(Box.createHorizontalStrut(12));
        linha1.add(seloDoModulo());

        JLabel d = new JLabel(descricao());
        d.setFont(Tema.NORMAL);
        d.setForeground(Tema.TEXTO_SUAVE);

        linha1.setAlignmentX(LEFT_ALIGNMENT);
        d.setAlignmentX(LEFT_ALIGNMENT);
        p.add(linha1);
        p.add(Box.createVerticalStrut(4));
        p.add(d);
        return p;
    }

    private JLabel seloDoModulo() {
        TipoModulo tipo = modulo().getTipo();
        return switch (tipo) {
            case OBRIGATORIO -> Tema.selo("NÚCLEO — TODOS OS CLIENTES", Tema.VERDE);
            case OPCIONAL -> Tema.selo("OPCIONAL", Tema.AZUL);
            case EXCLUSIVO -> Tema.selo("EXCLUSIVO " + modulo().getSegmentos().iterator().next().getDescricao().toUpperCase(), Tema.ROXO);
        };
    }

    private JComponent montarBotoes() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        JButton novo = Tema.botao("Novo", Tema.TEXTO_SUAVE);
        JButton salvar = Tema.botao("Salvar", Tema.VERDE);
        JButton excluir = Tema.botao("Excluir", Tema.VERMELHO);
        novo.addActionListener(e -> limpar());
        salvar.addActionListener(e -> salvar());
        excluir.addActionListener(e -> excluir());
        p.add(novo);
        p.add(salvar);
        p.add(excluir);
        return p;
    }

    private JComponent montarTabela() {
        modeloTabela = new DefaultTableModel(colunas(), 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(26);
        tabela.setFont(Tema.NORMAL);
        tabela.getTableHeader().setFont(Tema.NEGRITO);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setSelectionBackground(new Color(0xE3F0E9));
        tabela.setSelectionForeground(Tema.TEXTO);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int i = tabela.getSelectedRow();
            if (i >= 0 && i < itens.size()) {
                selecionado = itens.get(i);
                preencherFormulario(selecionado);
                status.setText("Editando registro #" + selecionado.getId());
            }
        });
        JScrollPane sp = new JScrollPane(tabela);
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    private void salvar() {
        try {
            T item = lerFormulario();
            boolean novo = selecionado == null;
            if (!novo) item.setId(selecionado.getId());
            repositorio().salvar(item);
            aposSalvar(item);
            atualizar();
            limpar();
            status.setText(novo ? "Registro incluído com sucesso." : "Registro alterado com sucesso.");
        } catch (ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Verifique os dados", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um registro na tabela.");
            return;
        }
        int r = JOptionPane.showConfirmDialog(this, "Excluir o registro selecionado?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) return;
        try {
            repositorio().excluir(selecionado.getId());
            atualizar();
            limpar();
            status.setText("Registro excluído.");
        } catch (ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Não permitido", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limpar() {
        selecionado = null;
        tabela.clearSelection();
        limparCampos();
        status.setText("Novo registro");
    }

    // ---------------- utilitários reutilizados pelas subclasses ----------------
    protected void adicionarCampo(String rotulo, JComponent campo) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = linhaFormulario;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(4, 0, 4, 10);
        JLabel l = new JLabel(rotulo);
        l.setFont(Tema.NEGRITO);
        l.setForeground(Tema.TEXTO_SUAVE);
        formulario.add(l, g);

        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        campo.setFont(Tema.NORMAL);
        formulario.add(campo, g);
        linhaFormulario++;
    }

    protected int lerInteiro(JTextField campo, String nome) {
        try {
            return Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException e) {
            throw new ValidacaoException(nome + ": informe um número inteiro.");
        }
    }

    protected double lerDecimal(JTextField campo, String nome) {
        try {
            return Double.parseDouble(campo.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new ValidacaoException(nome + ": informe um valor numérico.");
        }
    }

    protected <E extends Entidade> int idSelecionado(JComboBox<E> combo) {
        E e = combo.getItemAt(combo.getSelectedIndex());
        return e == null ? 0 : e.getId();
    }

    protected <E extends Entidade> void selecionarPorId(JComboBox<E> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    protected <E extends Entidade> void recarregarCombo(JComboBox<E> combo, List<E> itens) {
        combo.removeAllItems();
        for (E e : itens) combo.addItem(e);
    }

    protected String formatarMoeda(double v) { return String.format("R$ %.2f", v); }
}
