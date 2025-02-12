package com.example.kahootappclient;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder> {

    private List<Question> questionsList;

    public QuestionsAdapter(List<Question> questionsList) {
        this.questionsList = questionsList;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questionsList.get(position);
        holder.questionTextView.setText(question.getTexto());
        holder.answer1TextView.setText(question.getRespuestas().get(1));
        holder.answer2TextView.setText(question.getRespuestas().get(2));
        holder.answer3TextView.setText(question.getRespuestas().get(3));
        holder.answer4TextView.setText(question.getRespuestas().get(4));
    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView, answer1TextView, answer2TextView, answer3TextView, answer4TextView;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.questionTextView);
            answer1TextView = itemView.findViewById(R.id.answer1Button);
            answer2TextView = itemView.findViewById(R.id.answer2Button);
            answer3TextView = itemView.findViewById(R.id.answer3Button);
            answer4TextView = itemView.findViewById(R.id.answer4Button);
        }
    }
}