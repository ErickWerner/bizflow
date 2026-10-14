package br.pucpr.bizflow.ui;

import br.pucpr.bizflow.dao.ClienteDAO;
import br.pucpr.bizflow.dao.PetDAO;
import br.pucpr.bizflow.dao.Repositorio;
import br.pucpr.bizflow.model.Cliente;
import br.pucpr.bizflow.model.Pet;
import br.pucpr.bizflow.variabilidade.Modulo;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.util.HashMap;
import java.util.Map;

/**
 * MÓDULO EXCLUSIVO DO PETSHOP.
 * Só aparece no menu quando o segmento do negócio é PETSHOP
 * (ver Modulo.PETS e GerenciadorVariabilidade.isVisivel).
 */
public class PetPanel extends AbstractCrudPanel<Pet> {
    private final PetDAO dao = new PetDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final JComboBox<Cliente> tutor = new JComboBox<>();
    private final JTextField nome = new JTextField(18);
    private final JComboBox<String> especie = new JComboBox<>(new String[]{"Cão", "Gato", "Ave", "Outro"});
    private final JTextField raca = new JTextField(18);
    private final JTextField vacina = new JTextField(18);
    private final Map<Integer, String> nomesTutores = new HashMap<>();

    @Override protected String titulo() { return "Pets e Controle de Vacinas"; }

    @Override protected String descricao() {
        return "Função exclusiva do segmento Petshop — não existe para salão, barbearia ou academia.";
    }

    @Override protected Modulo modulo() { return Modulo.PETS; }

    @Override protected String[] colunas() { return new String[]{"ID", "Pet", "Espécie", "Tutor", "Última vacina", "Situação"}; }

    @Override protected Object[] linha(Pet p) {
        return new Object[]{p.getId(), p.getNome(), p.getEspecie(), nomesTutores.getOrDefault(p.getTutorId(), "?"),
                p.getUltimaVacina(), p.vacinaAtrasada() ? "VACINA ATRASADA" : "Em dia"};
    }

    @Override protected void criarCampos() {
        adicionarCampo("Tutor", tutor);
        adicionarCampo("Nome do pet", nome);
        adicionarCampo("Espécie", especie);
        adicionarCampo("Raça", raca);
        adicionarCampo("Última vacina", vacina);
    }

    @Override protected void aoAtualizar() {
        recarregarCombo(tutor, clienteDAO.listar());
        nomesTutores.clear();
        for (int i = 0; i < tutor.getItemCount(); i++) nomesTutores.put(tutor.getItemAt(i).getId(), tutor.getItemAt(i).getNome());
    }

    @Override protected void preencherFormulario(Pet p) {
        selecionarPorId(tutor, p.getTutorId());
        nome.setText(p.getNome());
        especie.setSelectedItem(p.getEspecie());
        raca.setText(p.getRaca());
        vacina.setText(p.getUltimaVacina());
    }

    @Override protected Pet lerFormulario() {
        Pet p = new Pet();
        p.setTutorId(idSelecionado(tutor));
        p.setNome(nome.getText().trim());
        p.setEspecie((String) especie.getSelectedItem());
        p.setRaca(raca.getText().trim());
        p.setUltimaVacina(vacina.getText().trim());
        return p;
    }

    @Override protected void limparCampos() {
        nome.setText("");
        especie.setSelectedIndex(0);
        raca.setText("");
        vacina.setText("");
    }

    @Override protected Repositorio<Pet> repositorio() { return dao; }
}
