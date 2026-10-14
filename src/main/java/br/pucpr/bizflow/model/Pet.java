package br.pucpr.bizflow.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** EXCLUSIVO do segmento Petshop. O tutor é um Cliente. */
public class Pet extends Entidade {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private int tutorId;
    private String nome;
    private String especie;
    private String raca;
    private String ultimaVacina; // dd/MM/yyyy

    @Override
    public void validar() {
        exigir(tutorId > 0, "Selecione o tutor.");
        exigir(!vazio(nome), "Informe o nome do pet.");
        exigir(!vazio(especie), "Informe a espécie.");
        exigir(vazio(ultimaVacina) || dataVacina() != null, "Data da vacina inválida (dd/MM/aaaa).");
    }

    public LocalDate dataVacina() {
        try { return LocalDate.parse(ultimaVacina, FORMATO); } catch (Exception e) { return null; }
    }

    /** Regra de negócio exclusiva do petshop: vacina vale 1 ano. */
    public boolean vacinaAtrasada() {
        LocalDate d = dataVacina();
        return d == null || d.plusYears(1).isBefore(LocalDate.now());
    }

    public int getTutorId() { return tutorId; }
    public void setTutorId(int tutorId) { this.tutorId = tutorId; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }
    public String getUltimaVacina() { return ultimaVacina; }
    public void setUltimaVacina(String ultimaVacina) { this.ultimaVacina = ultimaVacina; }
}
