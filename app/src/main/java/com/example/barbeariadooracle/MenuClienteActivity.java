package com.example.barbeariadooracle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MenuClienteActivity extends AppCompatActivity {

    private TextView tvSaudacaoCliente;
    private CardView cardNovoAgendamento, cardMeusAgendamentos;
    private int idCliente;
    private String nomeCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_cliente);

        tvSaudacaoCliente = findViewById(R.id.tvSaudacaoCliente);
        cardNovoAgendamento = findViewById(R.id.cardNovoAgendamento);
        cardMeusAgendamentos = findViewById(R.id.cardMeusAgendamentos);

        // Recebe os dados passados pelo Login
        idCliente = getIntent().getIntExtra("ID_CLIENTE", 0);
        nomeCliente = getIntent().getStringExtra("NOME_CLIENTE");

        if (nomeCliente != null) {
            tvSaudacaoCliente.setText("Olá, " + nomeCliente + "!");
        }

        cardNovoAgendamento.setOnClickListener(v -> {
            Intent intent = new Intent(MenuClienteActivity.this, NovoAgendamentoActivity.class);
            intent.putExtra("ID_CLIENTE", idCliente);
            startActivity(intent);
        });

        cardMeusAgendamentos.setOnClickListener(v -> {
            Intent intent = new Intent(MenuClienteActivity.this, HistoricoClienteActivity.class);
            intent.putExtra("ID_CLIENTE", idCliente);
            startActivity(intent);
        });
    }
}