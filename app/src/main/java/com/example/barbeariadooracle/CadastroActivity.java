package com.example.barbeariadooracle;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CadastroActivity extends AppCompatActivity {

    private EditText etNome, etEmailCad, etSenhaCad, etTelefone, etCpf, etDataNascimento;
    private Button btnSalvarCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        etNome = findViewById(R.id.etNome);
        etEmailCad = findViewById(R.id.etEmailCad);
        etSenhaCad = findViewById(R.id.etSenhaCad);
        etTelefone = findViewById(R.id.etTelefone);
        etCpf = findViewById(R.id.etCpf);
        etDataNascimento = findViewById(R.id.etDataNascimento);
        btnSalvarCadastro = findViewById(R.id.btnSalvarCadastro);

        btnSalvarCadastro.setOnClickListener(v -> cadastrarCliente());
    }

    private void cadastrarCliente() {
        String nome = etNome.getText().toString().trim();
        String email = etEmailCad.getText().toString().trim();
        String senha = etSenhaCad.getText().toString().trim();
        String telefone = etTelefone.getText().toString().trim();
        String cpf = etCpf.getText().toString().trim();
        String dataNascimento = etDataNascimento.getText().toString().trim();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha Nome, E-mail e Senha!", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Connection conn = Databaseconnection.connect();
            if (conn == null) {
                runOnUiThread(() -> Toast.makeText(CadastroActivity.this, "Erro de conexão com o banco!", Toast.LENGTH_SHORT).show());
                return;
            }

            try {
                String sql = "INSERT INTO CLIENTE (nome, cpf, telefone, email, senha, data_cadastro, pontuacao, data_de_nascimento) VALUES (?, ?, ?, ?, ?, GETDATE(), 0, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nome);
                stmt.setString(2, cpf);
                stmt.setString(3, telefone);
                stmt.setString(4, email);
                stmt.setString(5, senha);

                // Trata caso o campo de data fique vazio
                if (dataNascimento.isEmpty()) {
                    stmt.setNull(6, java.sql.Types.DATE);
                } else {
                    stmt.setString(6, dataNascimento); // Formato esperado: YYYY-MM-DD
                }

                stmt.executeUpdate();
                conn.close();

                runOnUiThread(() -> {
                    Toast.makeText(CadastroActivity.this, "Cliente cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(CadastroActivity.this, "Erro ao cadastrar: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}