package br.pucpr.bizflow.ui;

import br.pucpr.bizflow.dao.Repositorio;
import br.pucpr.bizflow.dao.ServicoDAO;
import br.pucpr.bizflow.model.Servico;
import br.pucpr.bizflow.variabilidade.GerenciadorVariabilidade;
import br.pucpr.bizflow.variabilidade.Modulo;

import javax.swing.JComboBox;
import javax.swing.JTextField;

/** CRUD de Serviços. As categorias sugeridas vêm do segmento. */
public class ServicoPanel extends AbstractCrudPanel<Servico> {
    private final ServicoDAO dao = new ServicoDAO();
    private final JTextField nome = new JTextField(18);
    private final JComboBox<String> categoria = new JComboBox<>(
            GerenciadorVariabilidade.getInstancia().getNegocioAtual().getSegmento().getCategorias());
    private final JTextField duracao = new JTextField(18);
    private final JTextField preco = new JTextField(18);

    @Override protected String titulo() { return "Catálogo de Serviços"; }

    @Override protected String descricao() {
        return "Tela universal — as categorias do combo variam conforme o segmento do negócio.";
    }

    @Override protected Modulo modulo() { return Modulo.SERVICOS; }

    @Override protected String[] colunas() { return new String[]{"ID", "Serviço", "Categoria", "Duração", "Preço"}; }

    @Override protected Object[] linha(Servico s) {
        return new Object[]{s.getId(), s.getNome(), s.getCategoria(), s.getDuracaoMinutos() + " min", s.precoFormatado()};
    }

    @Override protected void criarCampos() {
        categoria.setEditable(true);
        adicionarCampo("Nome", nome);
        adicionarCampo("Categoria", categoria);
        adicionarCampo("Duração (min)", duracao);
        adicionarCampo("Preço (R$)", preco);
    }

    @Override protected void preencherFormulario(Servico s) {
        nome.setText(s.getNome());
        categoria.setSelectedItem(s.getCategoria());
        duracao.setText(String.valueOf(s.getDuracaoMinutos()));
        preco.setText(String.format("%.2f", s.getPreco()));
    }

    @Override protected Servico lerFormulario() {
        Object cat = categoria.getSelectedItem();
        return new Servico(nome.getText().trim(), cat == null ? "" : cat.toString(),
                lerInteiro(duracao, "Duração"), lerDecimal(preco, "Preço"));
    }

    @Override protected void limparCampos() {
        nome.setText("");
        categoria.setSelectedIndex(0);
        duracao.setText("");
        preco.setText("");
    }

    @Override protected Repositorio<Servico> repositorio() { return dao; }
}
