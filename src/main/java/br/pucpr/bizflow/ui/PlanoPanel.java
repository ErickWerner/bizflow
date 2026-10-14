package br.pucpr.bizflow.ui;

import br.pucpr.bizflow.dao.PlanoDAO;
import br.pucpr.bizflow.dao.Repositorio;
import br.pucpr.bizflow.model.Plano;
import br.pucpr.bizflow.variabilidade.Modulo;

import javax.swing.JTextField;

/** MÓDULO EXCLUSIVO DA ACADEMIA — planos de mensalidade. */
public class PlanoPanel extends AbstractCrudPanel<Plano> {
    private final PlanoDAO dao = new PlanoDAO();
    private final JTextField nome = new JTextField(18);
    private final JTextField valor = new JTextField(18);
    private final JTextField meses = new JTextField(18);
    private final JTextField aulas = new JTextField(18);

    @Override protected String titulo() { return "Planos e Mensalidades"; }

    @Override protected String descricao() {
        return "Função exclusiva do segmento Academia — os demais clientes não têm este módulo.";
    }

    @Override protected Modulo modulo() { return Modulo.PLANOS; }

    @Override protected String[] colunas() { return new String[]{"ID", "Plano", "Mensal", "Meses", "Aulas/sem", "Total"}; }

    @Override protected Object[] linha(Plano p) {
        return new Object[]{p.getId(), p.getNome(), formatarMoeda(p.getValorMensal()), p.getDuracaoMeses(),
                p.getAulasPorSemana(), formatarMoeda(p.valorTotal())};
    }

    @Override protected void criarCampos() {
        adicionarCampo("Nome do plano", nome);
        adicionarCampo("Valor mensal (R$)", valor);
        adicionarCampo("Duração (meses)", meses);
        adicionarCampo("Aulas por semana", aulas);
    }

    @Override protected void preencherFormulario(Plano p) {
        nome.setText(p.getNome());
        valor.setText(String.format("%.2f", p.getValorMensal()));
        meses.setText(String.valueOf(p.getDuracaoMeses()));
        aulas.setText(String.valueOf(p.getAulasPorSemana()));
    }

    @Override protected Plano lerFormulario() {
        Plano p = new Plano();
        p.setNome(nome.getText().trim());
        p.setValorMensal(lerDecimal(valor, "Valor mensal"));
        p.setDuracaoMeses(lerInteiro(meses, "Duração"));
        p.setAulasPorSemana(lerInteiro(aulas, "Aulas por semana"));
        return p;
    }

    @Override protected void limparCampos() {
        nome.setText("");
        valor.setText("");
        meses.setText("");
        aulas.setText("");
    }

    @Override protected Repositorio<Plano> repositorio() { return dao; }
}
