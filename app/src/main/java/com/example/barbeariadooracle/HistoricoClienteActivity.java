package com.example.barbeariadooracle;

import android.os.Bundle;
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

public class HistoricoClienteActivity extends AppCompatActivity {

    private RecyclerView rvHistoricoCliente;
    private int idCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_cliente);

        rvHistoricoCliente = findViewById(R.id.rvHistoricoCliente);
        rvHistoricoCliente.setLayoutManager(new LinearLayoutManager(this));

        idCliente = getIntent().getIntExtra("ID_CLIENTE", 0);

        carregarHistoricoCliente();
    }

    private void carregarHistoricoCliente() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Connection conn = Databaseconnection.connect();
            if (conn == null) {
                runOnUiThread(() -> Toast.makeText(HistoricoClienteActivity.this, "Erro de conexão com o banco!", Toast.LENGTH_SHORT).show());
                return;
            }

            try {
                // Busca todos os agendamentos do cliente logado com o nome do barbeiro e do serviço
                String sql = "SELECT a.data_hora, f.nome AS barbeiro, s.descricao AS servico, a.status " +
                        "FROM AGENDAMENTO a " +
                        "JOIN FUNCIONARIO f ON a.id_funcionario = f.id_funcionario " +
                        "JOIN SERVICO s ON a.id_servico = s.id_servico " +
                        "WHERE a.id_cliente = ? " +
                        "ORDER BY a.data_hora DESC";

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, idCliente);
                ResultSet rs = stmt.executeQuery();

                List<Agendamento> lista = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                while (rs.next()) {
                    String dataHoraFormatted = sdf.format(rs.getTimestamp("data_hora"));
                    String barbeiro = rs.getString("barbeiro");
                    String servico = rs.getString("servico");
                    String status = rs.getString("status");

                    lista.add(new Agendamento(dataHoraFormatted, barbeiro, servico, status));
                }

                conn.close();

                runOnUiThread(() -> {
                    if (lista.isEmpty()) {
                        Toast.makeText(HistoricoClienteActivity.this, "Você ainda não possui agendamentos.", Toast.LENGTH_SHORT).show();
                    }
                    HistoricoClienteAdapter adapter = new HistoricoClienteAdapter(lista);
                    rvHistoricoCliente.setAdapter(adapter);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(HistoricoClienteActivity.this, "Erro SQL: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}