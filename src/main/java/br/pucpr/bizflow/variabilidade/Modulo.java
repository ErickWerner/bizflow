package br.pucpr.bizflow.variabilidade;

import java.util.EnumSet;
import java.util.Set;

/**
 * Modelo de features (feature model) da Linha de Produto BizFlow.
 * Cada módulo declara: tipo, segmentos onde existe e perfis que podem vê-lo.
 */
public enum Modulo {
    CLIENTES("Clientes", TipoModulo.OBRIGATORIO, todos(), perfis(Perfil.DONO, Perfil.RECEPCAO), true),
    SERVICOS("Serviços", TipoModulo.OBRIGATORIO, todos(), perfis(Perfil.DONO, Perfil.RECEPCAO), true),
    PROFISSIONAIS("Profissionais", TipoModulo.OBRIGATORIO, todos(), perfis(Perfil.DONO), true),
    AGENDAMENTOS("Agendamentos", TipoModulo.OBRIGATORIO, todos(), EnumSet.allOf(Perfil.class), true),

    PETS("Pets e Vacinas", TipoModulo.EXCLUSIVO, EnumSet.of(Segmento.PETSHOP),
            EnumSet.allOf(Perfil.class), true),
    PLANOS("Planos e Mensalidades", TipoModulo.EXCLUSIVO, EnumSet.of(Segmento.ACADEMIA),
            perfis(Perfil.DONO, Perfil.RECEPCAO), true),

    RELATORIOS("Relatórios", TipoModulo.OPCIONAL, todos(), perfis(Perfil.DONO), true),
    NOTIFICACAO_WHATSAPP("Notificação por WhatsApp", TipoModulo.OPCIONAL, todos(),
            EnumSet.allOf(Perfil.class), false),

    CONFIGURACAO("Configuração da LPS", TipoModulo.OBRIGATORIO, todos(), perfis(Perfil.DONO), true);

    private final String descricao;
    private final TipoModulo tipo;
    private final Set<Segmento> segmentos;
    private final Set<Perfil> perfis;
    private final boolean temTela;

    Modulo(String descricao, TipoModulo tipo, Set<Segmento> segmentos, Set<Perfil> perfis, boolean temTela) {
        this.descricao = descricao;
        this.tipo = tipo;
        this.segmentos = segmentos;
        this.perfis = perfis;
        this.temTela = temTela;
    }

    private static Set<Segmento> todos() { return EnumSet.allOf(Segmento.class); }
    private static Set<Perfil> perfis(Perfil p1, Perfil... outros) { return EnumSet.of(p1, outros); }

    public boolean existeNoSegmento(Segmento s) { return segmentos.contains(s); }
    public boolean permitidoPara(Perfil p) { return perfis.contains(p); }
    public boolean isConfiguravel() { return tipo != TipoModulo.OBRIGATORIO; }

    public String getDescricao() { return descricao; }
    public TipoModulo getTipo() { return tipo; }
    public Set<Perfil> getPerfis() { return perfis; }
    public Set<Segmento> getSegmentos() { return segmentos; }
    public boolean isTemTela() { return temTela; }
}
