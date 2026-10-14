package br.pucpr.bizflow.model;

import br.pucpr.bizflow.variabilidade.Segmento;

/** O cliente que contrata o BizFlow (salão, petshop, academia...). É o "tenant". */
public class Negocio extends Entidade {
    private String nome;
    private Segmento segmento;
    private String cidade;

    public Negocio() {}

    public Negocio(int id, String nome, Segmento segmento, String cidade) {
        this.id = id;
        this.negocioId = id;
        this.nome = nome;
        this.segmento = segmento;
        this.cidade = cidade;
    }

    @Override
    public void validar() {
        exigir(!vazio(nome), "Informe o nome do negócio.");
        exigir(segmento != null, "Informe o segmento do negócio.");
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Segmento getSegmento() { return segmento; }
    public void setSegmento(Segmento segmento) { this.segmento = segmento; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    @Override
    public String toString() { return nome + " (" + segmento.getDescricao() + ")"; }
}
