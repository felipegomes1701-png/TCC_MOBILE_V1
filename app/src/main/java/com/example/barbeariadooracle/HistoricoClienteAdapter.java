package com.example.barbeariadooracle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoricoClienteAdapter extends RecyclerView.Adapter<HistoricoClienteAdapter.ViewHolder> {

    private List<Agendamento> listaAgendamentos;

    public HistoricoClienteAdapter(List<Agendamento> listaAgendamentos) {
        this.listaAgendamentos = listaAgendamentos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historico_cliente, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Agendamento agendamento = listaAgendamentos.get(position);
        holder.tvDataHoraHist.setText("Data: " + agendamento.getDataHora());
        holder.tvBarbeiroHist.setText("Barbeiro: " + agendamento.getNomeCliente()); // Aqui reusamos a propriedade nomeCliente para armazenar o nome do Barbeiro
        holder.tvServicoHist.setText("Serviço: " + agendamento.getNomeServico());
        holder.tvStatusHist.setText("Status: " + agendamento.getStatus());
    }

    @Override
    public int getItemCount() {
        return listaAgendamentos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDataHoraHist, tvBarbeiroHist, tvServicoHist, tvStatusHist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDataHoraHist = itemView.findViewById(R.id.tvDataHoraHist);
            tvBarbeiroHist = itemView.findViewById(R.id.tvBarbeiroHist);
            tvServicoHist = itemView.findViewById(R.id.tvServicoHist);
            tvStatusHist = itemView.findViewById(R.id.tvStatusHist);
        }
    }
}