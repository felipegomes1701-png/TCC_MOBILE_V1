package com.example.barbeariadooracle;

public class Agendamento {
    private String dataHora;
    private String nomeCliente;
    private String nomeServico;
    private String status;

    public Agendamento(String dataHora, String nomeCliente, String nomeServico, String status) {
        this.dataHora = dataHora;
        this.nomeCliente = nomeCliente;
        this.nomeServico = nomeServico;
        this.status = status;
    }

    public String getDataHora() { return dataHora; }
    public String getNomeCliente() { return nomeCliente; }
    public String getNomeServico() { return nomeServico; }
    public String getStatus() { return status; }
}