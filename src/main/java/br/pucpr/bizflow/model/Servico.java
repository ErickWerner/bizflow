package br.pucpr.bizflow.model;

import java.text.NumberFormat;
import java.util.Locale;

/** Serviço do catálogo (corte, banho e tosa, aula de spinning...). */
public class Servico extends Entidade {
    private String nome;
    private String categoria;
    private int duracaoMinutos;
    private double preco;

    public Servico() {}

    public Servico(String nome, String categoria, int duracaoMinutos, double preco) {
        this.nome = nome;
        this.categoria = categoria;
        this.duracaoMinutos = duracaoMinutos;
        this.preco = preco;
    }

    @Override
    public void validar() {
        exigir(!vazio(nome), "Informe o nome do serviço.");
        exigir(duracaoMinutos > 0, "A duração deve ser maior que zero.");
        exigir(preco >= 0, "O preço não pode ser negativo.");
    }

    public String precoFormatado() {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(preco);
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public int getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(int duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }
    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    @Override
    public String toString() { return nome + " — " + precoFormatado(); }
}
