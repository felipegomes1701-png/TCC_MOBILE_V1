package com.example.barbeariadooracle;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AgendaActivity extends AppCompatActivity {

    private TextView tvTituloAgenda;
    private RecyclerView rvAgenda;
    private int idFuncionario;
    private String nomeFuncionario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        tvTituloAgenda = findViewById(R.id.tvTituloAgenda);
        rvAgenda = findViewById(R.id.rvAgenda);
        rvAgenda.setLayoutManager(new LinearLayoutManager(this));

        idFuncionario = getIntent().getIntExtra("ID_FUNCIONARIO", 0);
        nomeFuncionario = getIntent().getStringExtra("NOME_FUNCIONARIO");

        if (nomeFuncionario != null) {
            tvTituloAgenda.setText("Agenda: " + nomeFuncionario);
        }

        carregarAgendaSemana();
    }

    private void carregarAgendaSemana() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Connection conn = Databaseconnection.connect();
            if (conn == null) {
                runOnUiThread(() -> Toast.makeText(AgendaActivity.this, "Erro de conexão!", Toast.LENGTH_SHORT).show());
                return;
            }

            try {
                // Consulta agendamentos a partir de hoje até 7 dias pra frente
                String sql = "SELECT a.data_hora, c.nome AS cliente, s.descricao AS servico, a.status " +
                        "FROM AGENDAMENTO a " +
                        "JOIN CLIENTE c ON a.id_cliente = c.id_cliente " +
                        "JOIN SERVICO s ON a.id_servico = s.id_servico " +
                        "WHERE a.id_funcionario = ? AND a.data_hora >= CAST(GETDATE() AS DATE) " +
                        "ORDER BY a.data_hora ASC";

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, idFuncionario);
                ResultSet rs = stmt.executeQuery();

                List<Agendamento> lista = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                while (rs.next()) {
                    String dataHoraFormatted = sdf.format(rs.getTimestamp("data_hora"));
                    String cliente = rs.getString("cliente");
                    String servico = rs.getString("servico");
                    String status = rs.getString("status");

                    lista.add(new Agendamento(dataHoraFormatted, cliente, servico, status));
                }

                conn.close();

                runOnUiThread(() -> {
                    if (lista.isEmpty()) {
                        Toast.makeText(AgendaActivity.this, "Nenhum agendamento para esta semana.", Toast.LENGTH_SHORT).show();
                    }
                    AgendamentoAdapter adapter = new AgendamentoAdapter(lista);
                    rvAgenda.setAdapter(adapter);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(AgendaActivity.this, "Erro SQL: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}