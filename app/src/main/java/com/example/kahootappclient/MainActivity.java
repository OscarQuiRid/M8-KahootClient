package com.example.kahootappclient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText inputID, inputName;
    private TextView textViewErrorID, textViewErrorName;
    private Button buttonComezar;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar vistas
        TextInputLayout inputIDLayout = findViewById(R.id.inputID);
        TextInputLayout inputNameLayout = findViewById(R.id.inputName);
        inputID = (TextInputEditText) inputIDLayout.getEditText();
        inputName = (TextInputEditText) inputNameLayout.getEditText();
        textViewErrorID = findViewById(R.id.textViewErrorID);
        textViewErrorName = findViewById(R.id.textViewErrorName);
        buttonComezar = findViewById(R.id.buttonComezar);

        // Inicializar referencia a la base de datos
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        // Configurar validación de ID y nombre
        setupIDValidation();
        setupNameValidation();

        // Configurar el botón para comenzar el juego
        buttonComezar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });

        // Evitar que el usuario use Enter en los campos de texto
        disableEnterKey(inputID);
        disableEnterKey(inputName);
    }

    // Configurar validación de ID
    private void setupIDValidation() {
        inputID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkIDAvailability(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Verificar disponibilidad del ID
    private void checkIDAvailability(final String id) {
        if (id.isEmpty()) {
            textViewErrorID.setVisibility(View.GONE);
            inputName.setEnabled(false);
            return;
        }

        databaseReference.child("salas").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    textViewErrorID.setVisibility(View.GONE);
                    inputName.setEnabled(true);
                } else {
                    textViewErrorID.setText("ID de la partida no existe");
                    textViewErrorID.setVisibility(View.VISIBLE);
                    inputName.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", "Error checking ID availability", databaseError.toException());
            }
        });
    }

    // Configurar validación de nombre
    private void setupNameValidation() {
        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkNameAvailability(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Verificar disponibilidad del nombre
    private void checkNameAvailability(final String name) {
        final String id = inputID.getText().toString();
        if (name.isEmpty() || id.isEmpty()) {
            textViewErrorName.setVisibility(View.GONE);
            buttonComezar.setEnabled(false);
            return;
        }

        databaseReference.child("salas").child(id).child("jugadores").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean nameExists = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(name)) {
                        nameExists = true;
                        break;
                    }
                }
                if (nameExists) {
                    textViewErrorName.setText("Nombre no disponible");
                    textViewErrorName.setVisibility(View.VISIBLE);
                    buttonComezar.setEnabled(false);
                } else {
                    textViewErrorName.setVisibility(View.GONE);
                    buttonComezar.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", "Error checking name availability", databaseError.toException());
            }
        });
    }

    // Iniciar el juego
    private void startGame() {
        String id = inputID.getText().toString();
        String name = inputName.getText().toString();

        if (!id.isEmpty() && !name.isEmpty()) {
            // Agregar jugador a la base de datos
            DatabaseReference playerRef = databaseReference.child("salas").child(id).child("jugadores").child(name);
            playerRef.child("puntos").setValue(0);
            playerRef.child("respuestas").setValue(new HashMap<String, Object>());

            Intent intent = new Intent(MainActivity.this, ActivityJuego.class);
            intent.putExtra("partidaId", id);
            intent.putExtra("jugadorName", name);
            startActivity(intent);
        }
    }

    // Deshabilitar la tecla Enter en los campos de texto
    private void disableEnterKey(TextInputEditText editText) {
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });
    }
}