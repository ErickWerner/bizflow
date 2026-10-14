package br.pucpr.bizflow.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Um horário marcado: liga cliente + serviço + profissional (3 chaves estrangeiras). */
public class Agendamento extends Entidade {
    public static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int clienteId;
    private int servicoId;
    private int profissionalId;
    private String dataHora;           // "dd/MM/yyyy HH:mm"
    private StatusAgendamento status = StatusAgendamento.AGENDADO;
    private FormaPagamento formaPagamento = FormaPagamento.PIX;
    private double valorFinal;

    @Override
    public void validar() {
        exigir(clienteId > 0, "Selecione o cliente.");
        exigir(servicoId > 0, "Selecione o serviço.");
        exigir(profissionalId > 0, "Selecione o profissional.");
        exigir(dataHoraConvertida() != null, "Data/hora inválida. Use o formato dd/MM/aaaa HH:mm.");
    }

    public LocalDateTime dataHoraConvertida() {
        try {
            return LocalDateTime.parse(dataHora, FORMATO);
        } catch (DateTimeParseException | NullPointerException e) {
            return null;
        }
    }

    public boolean mesmoHorario(Agendamento outro) {
        return dataHora != null && dataHora.equals(outro.dataHora) && id != outro.id;
    }

    public boolean isAtivo() { return status != StatusAgendamento.CANCELADO; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }
    public int getServicoId() { return servicoId; }
    public void setServicoId(int servicoId) { this.servicoId = servicoId; }
    public int getProfissionalId() { return profissionalId; }
    public void setProfissionalId(int profissionalId) { this.profissionalId = profissionalId; }
    public String getDataHora() { return dataHora; }
    public void setDataHora(String dataHora) { this.dataHora = dataHora; }
    public StatusAgendamento getStatus() { return status; }
    public void setStatus(StatusAgendamento status) { this.status = status; }
    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(FormaPagamento formaPagamento) { this.formaPagamento = formaPagamento; }
    public double getValorFinal() { return valorFinal; }
    public void setValorFinal(double valorFinal) { this.valorFinal = valorFinal; }
}
