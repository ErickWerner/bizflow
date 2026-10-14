package br.pucpr.bizflow.variabilidade;

import br.pucpr.bizflow.strategy.agenda.AgendamentoEmGrupo;
import br.pucpr.bizflow.strategy.agenda.AgendamentoIndividual;
import br.pucpr.bizflow.strategy.agenda.RegraAgendamentoStrategy;

/**
 * PONTO CENTRAL DA VARIABILIDADE POR CLIENTE.
 * Cada segmento muda rótulos, categorias e a regra de agendamento,
 * sem nenhum "if (salao) ... else if (petshop)" espalhado pelo código.
 */
public enum Segmento {
    SALAO("Salão de Beleza", "Cliente", "Profissional",
            new String[]{"Cabelo", "Unhas", "Estética"}),
    PETSHOP("Petshop", "Tutor", "Tosador / Veterinário",
            new String[]{"Banho", "Tosa", "Veterinário"}),
    BARBEARIA("Barbearia", "Cliente", "Barbeiro",
            new String[]{"Corte", "Barba", "Combo"}),
    ACADEMIA("Academia", "Aluno", "Instrutor",
            new String[]{"Musculação", "Aula em grupo", "Avaliação"});

    private final String descricao;
    private final String rotuloCliente;
    private final String rotuloProfissional;
    private final String[] categorias;

    Segmento(String descricao, String rotuloCliente, String rotuloProfissional, String[] categorias) {
        this.descricao = descricao;
        this.rotuloCliente = rotuloCliente;
        this.rotuloProfissional = rotuloProfissional;
        this.categorias = categorias;
    }

    /** Strategy escolhida conforme o segmento: academia aceita aula em grupo. */
    public RegraAgendamentoStrategy regraAgendamento() {
        return this == ACADEMIA ? new AgendamentoEmGrupo(10) : new AgendamentoIndividual();
    }

    public String getDescricao() { return descricao; }
    public String getRotuloCliente() { return rotuloCliente; }
    public String getRotuloProfissional() { return rotuloProfissional; }
    public String[] getCategorias() { return categorias.clone(); }

    @Override
    public String toString() { return descricao; }
}
