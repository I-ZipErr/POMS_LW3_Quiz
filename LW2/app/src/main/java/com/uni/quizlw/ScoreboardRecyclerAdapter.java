package com.uni.quizlw;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ScoreboardRecyclerAdapter extends RecyclerView.Adapter<ScoreboardRecyclerAdapter.ViewHolder>{

    private ArrayList<ScoreLine> scores_list = new ArrayList<>();

    public ScoreboardRecyclerAdapter(ArrayList<ScoreLine> scores_list) {
        this.scores_list = scores_list;
    }

    @NonNull
    @Override
    public ScoreboardRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_score_board, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreboardRecyclerAdapter.ViewHolder holder, int position) {
            ScoreLine scoreLine = scores_list.get(position);
            holder.number.setText(Integer.toString(position+1));
            holder.name.setText(scoreLine.getName());
            holder.score.setText(Integer.toString(scoreLine.getScore()));
            holder.maxScore.setText(Integer.toString(scoreLine.getMaxScore()));
    }

    @Override
    public int getItemCount() {
        return scores_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView number, name, score, maxScore;

        public ViewHolder(View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.list_position);
            name = itemView.findViewById(R.id.list_name);
            score = itemView.findViewById(R.id.list_score);
            maxScore = itemView.findViewById(R.id.list_max_score);
        }
    }
}
