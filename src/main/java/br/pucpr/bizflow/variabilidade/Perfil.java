package br.pucpr.bizflow.variabilidade;

/** Perfis de acesso — variabilidade por USUÁRIO. */
public enum Perfil {
    DONO("Dono / Administrador"),
    RECEPCAO("Recepção"),
    PROFISSIONAL("Profissional");

    private final String descricao;

    Perfil(String descricao) { this.descricao = descricao; }

    @Override
    public String toString() { return descricao; }
}
