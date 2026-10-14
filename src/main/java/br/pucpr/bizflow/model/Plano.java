package br.pucpr.bizflow.model;

/** EXCLUSIVO do segmento Academia: plano de mensalidade dos alunos. */
public class Plano extends Entidade {
    private String nome;
    private double valorMensal;
    private int duracaoMeses;
    private int aulasPorSemana;

    @Override
    public void validar() {
        exigir(!vazio(nome), "Informe o nome do plano.");
        exigir(valorMensal > 0, "O valor mensal deve ser maior que zero.");
        exigir(duracaoMeses > 0, "A duração deve ser de pelo menos 1 mês.");
        exigir(aulasPorSemana > 0 && aulasPorSemana <= 7, "Aulas por semana: de 1 a 7.");
    }

    public double valorTotal() { return valorMensal * duracaoMeses; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public double getValorMensal() { return valorMensal; }
    public void setValorMensal(double valorMensal) { this.valorMensal = valorMensal; }
    public int getDuracaoMeses() { return duracaoMeses; }
    public void setDuracaoMeses(int duracaoMeses) { this.duracaoMeses = duracaoMeses; }
    public int getAulasPorSemana() { return aulasPorSemana; }
    public void setAulasPorSemana(int aulasPorSemana) { this.aulasPorSemana = aulasPorSemana; }
}
