package com.example.kahootappclient;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ActivityJuego extends AppCompatActivity {

    private TextView timerTextView, questionTextView, textViewWaiting, questionCounterTextView;
    private Button answer1Button, answer2Button, answer3Button, answer4Button;
    private RecyclerView scoresRecyclerView;
    private DatabaseReference databaseReference;
    private String partidaId, jugadorName;
    private List<Player> playersList = new ArrayList<>();
    private PlayersAdapter playersAdapter;
    private Question currentQuestion;
    private int selectedAnswer = -1;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        timerTextView = findViewById(R.id.timerTextView);
        questionTextView = findViewById(R.id.questionTextView);
        textViewWaiting = findViewById(R.id.textViewWaiting);
        questionCounterTextView = findViewById(R.id.questionCounterTextView);
        answer1Button = findViewById(R.id.answer1Button);
        answer2Button = findViewById(R.id.answer2Button);
        answer3Button = findViewById(R.id.answer3Button);
        answer4Button = findViewById(R.id.answer4Button);
        scoresRecyclerView = findViewById(R.id.scoresRecyclerView);

        partidaId = getIntent().getStringExtra("partidaId");
        jugadorName = getIntent().getStringExtra("jugadorName");

        databaseReference = FirebaseDatabase.getInstance().getReference();

        scoresRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        playersAdapter = new PlayersAdapter(playersList);
        scoresRecyclerView.setAdapter(playersAdapter);

        listenForGameStart();
        listenForCurrentQuestion();
        listenForScores();

        setupAnswerButtons();
    }

    private void listenForGameStart() {
        databaseReference.child("salas").child(partidaId).child("currentQuestion").child("canAnswer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean canAnswer = dataSnapshot.getValue(Boolean.class);
                if (canAnswer != null && canAnswer) {
                    startTimer(30000);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ActivityJuego", "Error al escuchar el inicio del juego", databaseError.toException());
            }
        });
    }

    private void listenForCurrentQuestion() {
        databaseReference.child("salas").child(partidaId).child("currentQuestion").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentQuestion = dataSnapshot.getValue(Question.class);
                if (currentQuestion != null) {
                    resetAnswerButtons();
                    showQuestion(currentQuestion);
                    updateQuestionCounter(currentQuestion.getCurrentQuestionNumber(), currentQuestion.getTotalQuestions());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ActivityJuego", "Error al escuchar la pregunta actual", databaseError.toException());
            }
        });
    }

    private void listenForScores() {
        databaseReference.child("salas").child(partidaId).child("jugadores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                playersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.getKey();
                    Long puntos = snapshot.child("puntos").getValue(Long.class);
                    playersList.add(new Player(name, puntos != null ? puntos.intValue() : 0));
                }
                Collections.sort(playersList, new Comparator<Player>() {
                    @Override
                    public int compare(Player p1, Player p2) {
                        return Integer.compare(p2.getScore(), p1.getScore());
                    }
                });
                playersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ActivityJuego", "Error al escuchar las puntuaciones", databaseError.toException());
            }
        });
    }

    private void showQuestion(Question question) {
        textViewWaiting.setVisibility(View.GONE);
        questionTextView.setText(question.getTexto());
        answer1Button.setText(question.getRespuestas().get(1));
        answer2Button.setText(question.getRespuestas().get(2));
        answer3Button.setText(question.getRespuestas().get(3));
        answer4Button.setText(question.getRespuestas().get(4));
        findViewById(R.id.questionLayout).setVisibility(View.VISIBLE);
        questionCounterTextView.setVisibility(View.VISIBLE);
        timerTextView.setVisibility(View.VISIBLE);
        enableAnswerButtons(true);
    }

    private void startTimer(long duration) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(duration, 1000) {
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
            }

            public void onFinish() {
                timerTextView.setText("00:00");
                evaluateAnswer();
                enableAnswerButtons(false);
                scoresRecyclerView.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void setupAnswerButtons() {
        View.OnClickListener answerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAnswerButtons();
                v.setBackgroundColor(getResources().getColor(R.color.blue));
                selectedAnswer = Integer.parseInt(v.getTag().toString());
            }
        };

        answer1Button.setTag(1);
        answer2Button.setTag(2);
        answer3Button.setTag(3);
        answer4Button.setTag(4);

        answer1Button.setOnClickListener(answerClickListener);
        answer2Button.setOnClickListener(answerClickListener);
        answer3Button.setOnClickListener(answerClickListener);
        answer4Button.setOnClickListener(answerClickListener);
    }

    private void resetAnswerButtons() {
        answer1Button.setBackgroundColor(getResources().getColor(R.color.button_color));
        answer2Button.setBackgroundColor(getResources().getColor(R.color.button_color));
        answer3Button.setBackgroundColor(getResources().getColor(R.color.button_color));
        answer4Button.setBackgroundColor(getResources().getColor(R.color.button_color));
    }

    private void enableAnswerButtons(boolean enable) {
        answer1Button.setEnabled(enable);
        answer2Button.setEnabled(enable);
        answer3Button.setEnabled(enable);
        answer4Button.setEnabled(enable);
    }

    private void evaluateAnswer() {
        enableAnswerButtons(false);
        if (selectedAnswer == currentQuestion.getRespuestaCorrecta()) {
            findViewById(getResources().getIdentifier("answer" + currentQuestion.getRespuestaCorrecta() + "Button", "id", getPackageName())).setBackgroundColor(getResources().getColor(R.color.green));
            updatePlayerScore(20);
        } else {
            if (selectedAnswer != -1) {
                findViewById(getResources().getIdentifier("answer" + selectedAnswer + "Button", "id", getPackageName())).setBackgroundColor(getResources().getColor(R.color.red));
            }
            findViewById(getResources().getIdentifier("answer" + currentQuestion.getRespuestaCorrecta() + "Button", "id", getPackageName())).setBackgroundColor(getResources().getColor(R.color.green));
            updatePlayerScore(0);
        }
    }

    private void updatePlayerScore(final int pointsToAdd) {
        databaseReference.child("salas").child(partidaId).child("jugadores").child(jugadorName).child("puntos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer currentPoints = dataSnapshot.getValue(Integer.class);
                if (currentPoints != null) {
                    databaseReference.child("salas").child(partidaId).child("jugadores").child(jugadorName).child("puntos").setValue(currentPoints + pointsToAdd);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ActivityJuego", "Error al actualizar los puntos del jugador", databaseError.toException());
            }
        });
    }

    private void updateQuestionCounter(int currentQuestionNumber, int totalQuestions) {
        questionCounterTextView.setText(String.format("%d/%d", currentQuestionNumber, totalQuestions));
    }
}