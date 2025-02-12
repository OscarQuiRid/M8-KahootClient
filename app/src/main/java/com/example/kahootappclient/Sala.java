package com.example.kahootappclient;

import java.util.Map;

public class Sala {
    private String id;
    private String dificultad;
    private Map<String, Object> jugadores;

    public Sala() {
        // Default constructor required for calls to DataSnapshot.getValue(Sala.class)
    }

    public Sala(String id, String dificultad, Map<String, Object> jugadores) {
        this.id = id;
        this.dificultad = dificultad;
        this.jugadores = jugadores;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public Map<String, Object> getJugadores() {
        return jugadores;
    }

    public void setJugadores(Map<String, Object> jugadores) {
        this.jugadores = jugadores;
    }
}