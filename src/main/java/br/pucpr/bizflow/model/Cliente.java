package br.pucpr.bizflow.model;

/** Pessoa atendida pelo negócio (vira "Tutor" no petshop e "Aluno" na academia). */
public class Cliente extends Entidade {
    private String nome;
    private String telefone;
    private String email;
    private String observacoes;

    public Cliente() {}

    public Cliente(String nome, String telefone, String email, String observacoes) {
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.observacoes = observacoes;
    }

    @Override
    public void validar() {
        exigir(!vazio(nome), "Informe o nome.");
        exigir(!vazio(telefone) && telefone.replaceAll("\\D", "").length() >= 10,
                "Telefone inválido (use DDD + número).");
        exigir(vazio(email) || email.contains("@"), "E-mail inválido.");
    }

    /** Telefone só com dígitos — usado pela notificação por WhatsApp/SMS. */
    public String telefoneNormalizado() {
        return telefone == null ? "" : telefone.replaceAll("\\D", "");
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public String toString() { return nome; }
}
