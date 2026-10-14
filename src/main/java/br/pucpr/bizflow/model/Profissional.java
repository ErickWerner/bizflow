package br.pucpr.bizflow.model;

/** Quem executa o serviço (cabeleireira, tosador, instrutor...). */
public class Profissional extends Entidade {
    private String nome;
    private String especialidade;
    private String telefone;
    private boolean ativo = true;

    public Profissional() {}

    public Profissional(String nome, String especialidade, String telefone, boolean ativo) {
        this.nome = nome;
        this.especialidade = especialidade;
        this.telefone = telefone;
        this.ativo = ativo;
    }

    @Override
    public void validar() {
        exigir(!vazio(nome), "Informe o nome do profissional.");
        exigir(!vazio(especialidade), "Informe a especialidade.");
    }

    public boolean podeAtender() { return ativo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() { return nome + (ativo ? "" : " (inativo)"); }
}
