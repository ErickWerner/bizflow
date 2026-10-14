package br.pucpr.bizflow.model;

import br.pucpr.bizflow.variabilidade.Perfil;

/** Quem está usando o sistema. O perfil define o que ele enxerga (variabilidade por usuário). */
public class Usuario {
    private final String nome;
    private final Perfil perfil;

    public Usuario(String nome, Perfil perfil) {
        this.nome = nome;
        this.perfil = perfil;
    }

    public boolean isAdministrador() { return perfil == Perfil.DONO; }

    public String getNome() { return nome; }
    public Perfil getPerfil() { return perfil; }

    @Override
    public String toString() { return nome + " (" + perfil + ")"; }
}
