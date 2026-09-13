package com.example.barbeariadooracle;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NovoAgendamentoActivity extends AppCompatActivity {

    private Spinner spServico, spBarbeiro, spHorarios;
    private Button btnSelecionarData, btnConfirmarAgendamento;

    private Map<String, Integer> mapaServicos = new HashMap<>();
    private Map<String, Integer> mapaBarbeiros = new HashMap<>();

    private String dataSelecionada = "";
    private int idCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_agendamento);

        spServico = findViewById(R.id.spServico);
        spBarbeiro = findViewById(R.id.spBarbeiro);
        spHorarios = findViewById(R.id.spHorarios);
        btnSelecionarData = findViewById(R.id.btnSelecionarData);
        btnConfirmarAgendamento = findViewById(R.id.btnConfirmarAgendamento);

        idCliente = getIntent().getIntExtra("ID_CLIENTE", 0);

        btnSelecionarData.setOnClickListener(v -> abrirDatePicker());
        btnConfirmarAgendamento.setOnClickListener(v -> salvarAgendamento());

        carregarHorariosAtendimento();
        carregarServicosEBarbeiros();
    }

    private void carregarHorariosAtendimento() {
        List<String> listaHorarios = new ArrayList<>();
        // Gera horários das 08:00 às 19:00 de 30 em 30 minutos
        for (int hora = 8; hora <= 19; hora++) {
            listaHorarios.add(String.format("%02d:00", hora));
            if (hora < 19) {
                listaHorarios.add(String.format("%02d:30", hora));
            }
        }

        ArrayAdapter<String> adapterHorarios = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                listaHorarios
        );
        spHorarios.setAdapter(adapterHorarios);
    }

    private void abrirDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            dataSelecionada = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            btnSelecionarData.setText("Data: " + dayOfMonth + "/" + (month + 1) + "/" + year);
        }, ano, mes, dia);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void carregarServicosEBarbeiros() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Connection conn = Databaseconnection.connect();
            if (conn == null) {
                runOnUiThread(() -> Toast.makeText(NovoAgendamentoActivity.this, "Erro de conexão com o banco!", Toast.LENGTH_SHORT).show());
                return;
            }

            try {
                String sqlServicos = "SELECT id_servico, descricao FROM SERVICO";
                PreparedStatement stmtServ = conn.prepareStatement(sqlServicos);
                ResultSet rsServ = stmtServ.executeQuery();

                List<String> listaServicos = new ArrayList<>();
                mapaServicos.clear();
                while (rsServ.next()) {
                    int id = rsServ.getInt("id_servico");
                    String desc = rsServ.getString("descricao");
                    listaServicos.add(desc);
                    mapaServicos.put(desc, id);
                }

                String sqlBarbeiros = "SELECT id_funcionario, nome FROM FUNCIONARIO";
                PreparedStatement stmtBarb = conn.prepareStatement(sqlBarbeiros);
                ResultSet rsBarb = stmtBarb.executeQuery();

                List<String> listaBarbeiros = new ArrayList<>();
                mapaBarbeiros.clear();
                while (rsBarb.next()) {
                    int id = rsBarb.getInt("id_funcionario");
                    String nome = rsBarb.getString("nome");
                    listaBarbeiros.add(nome);
                    mapaBarbeiros.put(nome, id);
                }

                conn.close();

                runOnUiThread(() -> {
                    ArrayAdapter<String> adapterServico = new ArrayAdapter<>(NovoAgendamentoActivity.this, android.R.layout.simple_spinner_dropdown_item, listaServicos);
                    spServico.setAdapter(adapterServico);

                    ArrayAdapter<String> adapterBarbeiro = new ArrayAdapter<>(NovoAgendamentoActivity.this, android.R.layout.simple_spinner_dropdown_item, listaBarbeiros);
                    spBarbeiro.setAdapter(adapterBarbeiro);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(NovoAgendamentoActivity.this, "Erro ao carregar dados: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void salvarAgendamento() {
        if (dataSelecionada.isEmpty()) {
            Toast.makeText(this, "Por favor, escolha uma data!", Toast.LENGTH_SHORT).show();
            return;
        }

        String horaSelecionada = spHorarios.getSelectedItem() != null ? spHorarios.getSelectedItem().toString() : "";
        if (horaSelecionada.isEmpty()) {
            Toast.makeText(this, "Por favor, escolha um horário!", Toast.LENGTH_SHORT).show();
            return;
        }

        String servicoSelecionado = spServico.getSelectedItem() != null ? spServico.getSelectedItem().toString() : "";
        String barbeiroSelecionado = spBarbeiro.getSelectedItem() != null ? spBarbeiro.getSelectedItem().toString() : "";

        Integer idServico = mapaServicos.get(servicoSelecionado);
        Integer idFuncionario = mapaBarbeiros.get(barbeiroSelecionado);

        if (idServico == null || idFuncionario == null) {
            Toast.makeText(this, "Selecione um serviço e um barbeiro válidos!", Toast.LENGTH_SHORT).show();
            return;
        }

        String dataHoraStr = dataSelecionada + " " + horaSelecionada + ":00";

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Connection conn = Databaseconnection.connect();
            if (conn == null) {
                runOnUiThread(() -> Toast.makeText(NovoAgendamentoActivity.this, "Erro de conexão!", Toast.LENGTH_SHORT).show());
                return;
            }

            try {
                String sql = "INSERT INTO AGENDAMENTO (id_cliente, id_funcionario, id_servico, data_hora, status) VALUES (?, ?, ?, ?, 'Agendado')";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, idCliente);
                stmt.setInt(2, idFuncionario);
                stmt.setInt(3, idServico);

                // Envia como Timestamp nativo do JDBC para evitar conflito de conversão no SQL Server
                Timestamp timestamp = Timestamp.valueOf(dataHoraStr);
                stmt.setTimestamp(4, timestamp);

                stmt.executeUpdate();
                conn.close();

                runOnUiThread(() -> {
                    Toast.makeText(NovoAgendamentoActivity.this, "Agendamento realizado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(NovoAgendamentoActivity.this, "Erro SQL: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}