package br.pucpr.bizflow.ui;

import br.pucpr.bizflow.dao.AgendamentoDAO;
import br.pucpr.bizflow.dao.ClienteDAO;
import br.pucpr.bizflow.dao.ProfissionalDAO;
import br.pucpr.bizflow.dao.Repositorio;
import br.pucpr.bizflow.dao.ServicoDAO;
import br.pucpr.bizflow.model.Agendamento;
import br.pucpr.bizflow.model.Cliente;
import br.pucpr.bizflow.model.FormaPagamento;
import br.pucpr.bizflow.model.Pagamento;
import br.pucpr.bizflow.model.Profissional;
import br.pucpr.bizflow.model.Servico;
import br.pucpr.bizflow.model.StatusAgendamento;
import br.pucpr.bizflow.model.ValidacaoException;
import br.pucpr.bizflow.strategy.notificacao.NotificacaoStrategy;
import br.pucpr.bizflow.variabilidade.GerenciadorVariabilidade;
import br.pucpr.bizflow.variabilidade.Modulo;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * CRUD de Agendamentos — junta 3 tabelas (cliente, serviço, profissional)
 * e usa as 3 Strategies: regra de agenda, cálculo de pagamento e notificação.
 */
public class AgendamentoPanel extends AbstractCrudPanel<Agendamento> {
    private final AgendamentoDAO dao = new AgendamentoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ServicoDAO servicoDAO = new ServicoDAO();
    private final ProfissionalDAO profissionalDAO = new ProfissionalDAO();

    private final JComboBox<Cliente> cliente = new JComboBox<>();
    private final JComboBox<Servico> servico = new JComboBox<>();
    private final JComboBox<Profissional> profissional = new JComboBox<>();
    private final JTextField dataHora = new JTextField(18);
    private final JComboBox<StatusAgendamento> status = new JComboBox<>(StatusAgendamento.values());
    private final JComboBox<FormaPagamento> pagamento = new JComboBox<>(FormaPagamento.values());
    private final JLabel regra = new JLabel();

    private final Map<Integer, String> nomesClientes = new HashMap<>();
    private final Map<Integer, String> nomesServicos = new HashMap<>();

    private GerenciadorVariabilidade ctx() { return GerenciadorVariabilidade.getInstancia(); }

    @Override protected String titulo() { return "Agendamentos"; }

    @Override protected String descricao() {
        return "Regra de agenda, pagamento e notificação são Strategies escolhidas pela configuração do cliente.";
    }

    @Override protected Modulo modulo() { return Modulo.AGENDAMENTOS; }

    @Override protected String[] colunas() {
        return new String[]{"ID", "Data/Hora", ctx().rotuloCliente(), "Serviço", "Status", "Pagamento", "Valor"};
    }

    @Override protected Object[] linha(Agendamento a) {
        return new Object[]{a.getId(), a.getDataHora(), nomesClientes.getOrDefault(a.getClienteId(), "?"),
                nomesServicos.getOrDefault(a.getServicoId(), "?"), a.getStatus(), a.getFormaPagamento(),
                formatarMoeda(a.getValorFinal())};
    }

    @Override protected void criarCampos() {
        regra.setFont(Tema.PEQUENA);
        regra.setForeground(Tema.ROXO);
        adicionarCampo(ctx().rotuloCliente(), cliente);
        adicionarCampo("Serviço", servico);
        adicionarCampo(ctx().rotuloProfissional(), profissional);
        adicionarCampo("Data/hora", dataHora);
        adicionarCampo("Status", status);
        adicionarCampo("Pagamento", pagamento);
        adicionarCampo("Regra", regra);
        limparCampos();
    }

    @Override protected void aoAtualizar() {
        recarregarCombo(cliente, clienteDAO.listar());
        recarregarCombo(servico, servicoDAO.listar());
        recarregarCombo(profissional, profissionalDAO.listar());
        nomesClientes.clear();
        nomesServicos.clear();
        for (int i = 0; i < cliente.getItemCount(); i++) nomesClientes.put(cliente.getItemAt(i).getId(), cliente.getItemAt(i).getNome());
        for (int i = 0; i < servico.getItemCount(); i++) nomesServicos.put(servico.getItemAt(i).getId(), servico.getItemAt(i).getNome());
        regra.setText(ctx().regraAgendamento().descricao());
    }

    @Override protected void preencherFormulario(Agendamento a) {
        selecionarPorId(cliente, a.getClienteId());
        selecionarPorId(servico, a.getServicoId());
        selecionarPorId(profissional, a.getProfissionalId());
        dataHora.setText(a.getDataHora());
        status.setSelectedItem(a.getStatus());
        pagamento.setSelectedItem(a.getFormaPagamento());
    }

    @Override protected Agendamento lerFormulario() {
        Servico s = servico.getItemAt(servico.getSelectedIndex());
        if (s == null) throw new ValidacaoException("Cadastre um serviço antes de agendar.");
        Profissional p = profissional.getItemAt(profissional.getSelectedIndex());
        if (p != null && !p.podeAtender()) throw new ValidacaoException("Este profissional está inativo.");

        Agendamento a = new Agendamento();
        a.setClienteId(idSelecionado(cliente));
        a.setServicoId(s.getId());
        a.setProfissionalId(idSelecionado(profissional));
        a.setDataHora(dataHora.getText().trim());
        a.setStatus((StatusAgendamento) status.getSelectedItem());
        FormaPagamento forma = (FormaPagamento) pagamento.getSelectedItem();
        a.setFormaPagamento(forma);
        a.setValorFinal(new Pagamento(s.getPreco(), forma).valorFinal()); // Strategy de pagamento
        return a;
    }

    /** Gancho do Template Method: depois de salvar, notifica o cliente (Strategy de notificação). */
    @Override protected void aposSalvar(Agendamento a) {
        Cliente c = clienteDAO.buscarPorId(a.getClienteId());
        Servico s = servicoDAO.buscarPorId(a.getServicoId());
        NotificacaoStrategy canal = ctx().estrategiaNotificacao(c);
        String msg = "Olá " + c.getNome() + ", seu horário de " + s.getNome() + " está marcado para "
                + a.getDataHora() + " em " + ctx().getNegocioAtual().getNome() + ".";
        String enviado = canal.enviar(c, msg);
        String pag = new Pagamento(s.getPreco(), a.getFormaPagamento()).resumo();
        JOptionPane.showMessageDialog(this, "Agendamento salvo!\n\n" + pag + "\n\nNotificação via "
                + canal.canal() + ":\n" + enviado, "BizFlow", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override protected void limparCampos() {
        dataHora.setText(LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).format(Agendamento.FORMATO));
        status.setSelectedItem(StatusAgendamento.AGENDADO);
        pagamento.setSelectedItem(FormaPagamento.PIX);
    }

    @Override protected Repositorio<Agendamento> repositorio() { return dao; }
}
