package br.pucpr.bizflow.ui;

import br.pucpr.bizflow.dao.ClienteDAO;
import br.pucpr.bizflow.dao.Repositorio;
import br.pucpr.bizflow.model.Cliente;
import br.pucpr.bizflow.variabilidade.GerenciadorVariabilidade;
import br.pucpr.bizflow.variabilidade.Modulo;

import javax.swing.JTextField;

/** CRUD de Clientes. O rótulo muda por segmento: Cliente / Tutor / Aluno. */
public class ClientePanel extends AbstractCrudPanel<Cliente> {
    private final ClienteDAO dao = new ClienteDAO();
    private final JTextField nome = new JTextField(18);
    private final JTextField telefone = new JTextField(18);
    private final JTextField email = new JTextField(18);
    private final JTextField observacoes = new JTextField(18);

    private String rotulo() { return GerenciadorVariabilidade.getInstancia().rotuloCliente(); }

    @Override protected String titulo() { return "Cadastro de " + rotulo() + "s"; }

    @Override protected String descricao() {
        return "Tela universal — o nome da entidade varia por segmento (Cliente, Tutor, Aluno).";
    }

    @Override protected Modulo modulo() { return Modulo.CLIENTES; }

    @Override protected String[] colunas() { return new String[]{"ID", "Nome", "Telefone", "E-mail"}; }

    @Override protected Object[] linha(Cliente c) {
        return new Object[]{c.getId(), c.getNome(), c.getTelefone(), c.getEmail()};
    }

    @Override protected void criarCampos() {
        adicionarCampo("Nome", nome);
        adicionarCampo("Telefone", telefone);
        adicionarCampo("E-mail", email);
        adicionarCampo("Observações", observacoes);
    }

    @Override protected void preencherFormulario(Cliente c) {
        nome.setText(c.getNome());
        telefone.setText(c.getTelefone());
        email.setText(c.getEmail());
        observacoes.setText(c.getObservacoes());
    }

    @Override protected Cliente lerFormulario() {
        return new Cliente(nome.getText().trim(), telefone.getText().trim(),
                email.getText().trim(), observacoes.getText().trim());
    }

    @Override protected void limparCampos() {
        nome.setText("");
        telefone.setText("");
        email.setText("");
        observacoes.setText("");
    }

    @Override protected Repositorio<Cliente> repositorio() { return dao; }
}
