package br.pucpr.bizflow.ui;

import br.pucpr.bizflow.dao.ProfissionalDAO;
import br.pucpr.bizflow.dao.Repositorio;
import br.pucpr.bizflow.model.Profissional;
import br.pucpr.bizflow.variabilidade.GerenciadorVariabilidade;
import br.pucpr.bizflow.variabilidade.Modulo;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

/** CRUD de Profissionais (vira "Instrutor" na academia, "Barbeiro" na barbearia). */
public class ProfissionalPanel extends AbstractCrudPanel<Profissional> {
    private final ProfissionalDAO dao = new ProfissionalDAO();
    private final JTextField nome = new JTextField(18);
    private final JTextField especialidade = new JTextField(18);
    private final JTextField telefone = new JTextField(18);
    private final JCheckBox ativo = new JCheckBox("Atende atualmente", true);

    @Override protected String titulo() {
        return "Equipe — " + GerenciadorVariabilidade.getInstancia().rotuloProfissional();
    }

    @Override protected String descricao() {
        return "Tela universal, visível só para o perfil Dono (variabilidade por usuário).";
    }

    @Override protected Modulo modulo() { return Modulo.PROFISSIONAIS; }

    @Override protected String[] colunas() { return new String[]{"ID", "Nome", "Especialidade", "Telefone", "Ativo"}; }

    @Override protected Object[] linha(Profissional p) {
        return new Object[]{p.getId(), p.getNome(), p.getEspecialidade(), p.getTelefone(), p.isAtivo() ? "Sim" : "Não"};
    }

    @Override protected void criarCampos() {
        ativo.setOpaque(false);
        adicionarCampo("Nome", nome);
        adicionarCampo("Especialidade", especialidade);
        adicionarCampo("Telefone", telefone);
        adicionarCampo("Situação", ativo);
    }

    @Override protected void preencherFormulario(Profissional p) {
        nome.setText(p.getNome());
        especialidade.setText(p.getEspecialidade());
        telefone.setText(p.getTelefone());
        ativo.setSelected(p.isAtivo());
    }

    @Override protected Profissional lerFormulario() {
        return new Profissional(nome.getText().trim(), especialidade.getText().trim(),
                telefone.getText().trim(), ativo.isSelected());
    }

    @Override protected void limparCampos() {
        nome.setText("");
        especialidade.setText("");
        telefone.setText("");
        ativo.setSelected(true);
    }

    @Override protected Repositorio<Profissional> repositorio() { return dao; }
}
