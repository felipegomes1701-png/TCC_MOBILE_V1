package com.example.barbeariadooracle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AgendamentoAdapter extends RecyclerView.Adapter<AgendamentoAdapter.ViewHolder> {

    private List<Agendamento> listaAgendamentos;

    public AgendamentoAdapter(List<Agendamento> listaAgendamentos) {
        this.listaAgendamentos = listaAgendamentos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_agendamento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Agendamento agendamento = listaAgendamentos.get(position);
        holder.tvDataHora.setText("Data: " + agendamento.getDataHora());
        holder.tvCliente.setText("Cliente: " + agendamento.getNomeCliente());
        holder.tvServico.setText("Serviço: " + agendamento.getNomeServico());
        holder.tvStatus.setText("Status: " + agendamento.getStatus());
    }

    @Override
    public int getItemCount() {
        return listaAgendamentos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDataHora, tvCliente, tvServico, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDataHora = itemView.findViewById(R.id.tvDataHora);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvServico = itemView.findViewById(R.id.tvServico);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}