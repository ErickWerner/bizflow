package br.pucpr.bizflow.variabilidade;

import br.pucpr.bizflow.dao.ModuloDAO;
import br.pucpr.bizflow.model.Cliente;
import br.pucpr.bizflow.model.Negocio;
import br.pucpr.bizflow.model.Usuario;
import br.pucpr.bizflow.strategy.agenda.RegraAgendamentoStrategy;
import br.pucpr.bizflow.strategy.notificacao.NotificacaoEmail;
import br.pucpr.bizflow.strategy.notificacao.NotificacaoSms;
import br.pucpr.bizflow.strategy.notificacao.NotificacaoStrategy;
import br.pucpr.bizflow.strategy.notificacao.NotificacaoWhatsApp;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * SINGLETON #2 — guarda o contexto da sessão (qual negócio e qual usuário)
 * e responde "este módulo está disponível/visível?".
 *
 * Por que Singleton? Telas, DAOs e relatórios precisam consultar o MESMO
 * contexto. Se cada um criasse o seu, um poderia achar que o negócio é
 * o salão e outro o petshop.
 */
public final class GerenciadorVariabilidade {

    private static GerenciadorVariabilidade instancia;

    private Negocio negocioAtual;
    private Usuario usuarioAtual;
    private final Map<Modulo, Boolean> modulosAtivos = new EnumMap<>(Modulo.class);
    private final ModuloDAO moduloDAO = new ModuloDAO();

    private GerenciadorVariabilidade() {}

    public static synchronized GerenciadorVariabilidade getInstancia() {
        if (instancia == null) {
            instancia = new GerenciadorVariabilidade();
        }
        return instancia;
    }

    /** Chamado no login: carrega do banco quais módulos este negócio contratou. */
    public void iniciarSessao(Negocio negocio, Usuario usuario) {
        this.negocioAtual = negocio;
        this.usuarioAtual = usuario;
        modulosAtivos.clear();
        modulosAtivos.putAll(moduloDAO.carregar(negocio.getId()));
    }

    /** O módulo existe para o segmento E está ligado para este negócio? */
    public boolean isDisponivel(Modulo m) {
        if (negocioAtual == null || !m.existeNoSegmento(negocioAtual.getSegmento())) return false;
        if (m.getTipo() == TipoModulo.OBRIGATORIO) return true;
        return modulosAtivos.getOrDefault(m, false);
    }

    /** Disponível para o negócio E permitido para o perfil do usuário logado? */
    public boolean isVisivel(Modulo m) {
        return isDisponivel(m) && usuarioAtual != null && m.permitidoPara(usuarioAtual.getPerfil());
    }

    public List<Modulo> modulosVisiveisComTela() {
        List<Modulo> lista = new ArrayList<>();
        for (Modulo m : Modulo.values()) {
            if (m.isTemTela() && isVisivel(m)) lista.add(m);
        }
        return lista;
    }

    /** Liga/desliga um módulo configurável e persiste no banco. */
    public void definirAtivo(Modulo m, boolean ativo) {
        if (!m.isConfiguravel()) {
            throw new IllegalArgumentException(m.getDescricao() + " é obrigatório e não pode ser desligado.");
        }
        moduloDAO.salvar(negocioAtual.getId(), m, ativo);
        modulosAtivos.put(m, ativo);
    }

    // ---- Escolha das Strategies a partir da configuração ----

    public RegraAgendamentoStrategy regraAgendamento() {
        return negocioAtual.getSegmento().regraAgendamento();
    }

    public NotificacaoStrategy estrategiaNotificacao(Cliente cliente) {
        if (isDisponivel(Modulo.NOTIFICACAO_WHATSAPP)) return new NotificacaoWhatsApp();
        if (cliente.getEmail() != null && cliente.getEmail().contains("@")) return new NotificacaoEmail();
        return new NotificacaoSms();
    }

    public String rotuloCliente() { return negocioAtual.getSegmento().getRotuloCliente(); }
    public String rotuloProfissional() { return negocioAtual.getSegmento().getRotuloProfissional(); }

    public Negocio getNegocioAtual() { return negocioAtual; }
    public Usuario getUsuarioAtual() { return usuarioAtual; }

    public int negocioAtualId() {
        if (negocioAtual == null) throw new IllegalStateException("Nenhum negócio na sessão.");
        return negocioAtual.getId();
    }
}
