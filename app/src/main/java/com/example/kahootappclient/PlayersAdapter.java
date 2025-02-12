package com.example.kahootappclient;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder> {

    private List<Player> playersList;

    public PlayersAdapter(List<Player> playersList) {
        this.playersList = playersList;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = playersList.get(position);
        holder.nameTextView.setText(player.getName());
        holder.scoreTextView.setText(String.valueOf(player.getScore()));
    }

    @Override
    public int getItemCount() {
        return playersList.size();
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, scoreTextView;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
        }
    }
}