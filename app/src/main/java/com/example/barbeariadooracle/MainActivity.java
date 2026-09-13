package com.example.barbeariadooracle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etSenha;
    private Button btnLogin;
    private TextView tvCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
        btnLogin = findViewById(R.id.btnLogin);
        tvCadastrar = findViewById(R.id.tvCadastrar);

        // Ação do Botão Entrar
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String senha = etSenha.getText().toString().trim();

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(MainActivity.this, "Preencha e-mail e senha!", Toast.LENGTH_SHORT).show();
            } else {
                autenticarUsuario(email, senha);
            }
        });

        // Ação do Botão Cadastrar (Abre a tela de cadastro)
        tvCadastrar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CadastroActivity.class);
            startActivity(intent);
        });
    }

    private void autenticarUsuario(String email, String senha) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Connection conn = Databaseconnection.connect();

            if (conn == null) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Erro ao conectar ao banco!", Toast.LENGTH_SHORT).show());
                return;
            }

            try {
                // 1. VERIFICA SE É FUNCIONÁRIO
                String sqlFunc = "SELECT id_funcionario, nome FROM FUNCIONARIO WHERE email = ? AND senha = ?";
                PreparedStatement stmtFunc = conn.prepareStatement(sqlFunc);
                stmtFunc.setString(1, email);
                stmtFunc.setString(2, senha);
                ResultSet rsFunc = stmtFunc.executeQuery();

                if (rsFunc.next()) {
                    int idFuncionario = rsFunc.getInt("id_funcionario");
                    String nomeFuncionario = rsFunc.getString("nome");

                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Bem-vindo(a), " + nomeFuncionario + "!", Toast.LENGTH_SHORT).show();

                        // Direciona para a tela da Agenda passando dados do funcionário
                        Intent intent = new Intent(MainActivity.this, AgendaActivity.class);
                        intent.putExtra("ID_FUNCIONARIO", idFuncionario);
                        intent.putExtra("NOME_FUNCIONARIO", nomeFuncionario);
                        startActivity(intent);
                        finish(); // Fecha a tela de login
                    });
                    conn.close();
                    return;
                }

                // 2. SE NÃO FOR FUNCIONÁRIO, VERIFICA SE É CLIENTE
                String sqlCli = "SELECT id_cliente, nome FROM CLIENTE WHERE email = ? AND senha = ?";
                PreparedStatement stmtCli = conn.prepareStatement(sqlCli);
                stmtCli.setString(1, email);
                stmtCli.setString(2, senha);
                ResultSet rsCli = stmtCli.executeQuery();

                if (rsCli.next()) {
                    int idCliente = rsCli.getInt("id_cliente");
                    String nomeCliente = rsCli.getString("nome");

                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Bem-vindo(a), " + nomeCliente + "!", Toast.LENGTH_SHORT).show();

                        // Direcionará para o menu do cliente quando essa tela for criada
                         Intent intent = new Intent(MainActivity.this, MenuClienteActivity.class);
                         intent.putExtra("ID_CLIENTE", idCliente);
                         startActivity(intent);
                    });
                    conn.close();
                    return;
                }

                // 3. SE NÃO ENCONTRAR EM NENHUMA DAS TABELAS
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "E-mail ou senha incorretos!", Toast.LENGTH_SHORT).show());

                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Erro de SQL: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}