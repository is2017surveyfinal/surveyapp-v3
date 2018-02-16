package it.survey.survey_is.model;

import java.io.Serializable;


public class Risposta implements Serializable {
    private String id;
    private String risposta;
    private Boolean selezionata;

    public Risposta(String id, String risposta, Boolean selezionata) {
        this.id = id;
        this.risposta = risposta;
        this.selezionata = selezionata;
    }

    public Risposta() {
        this(null, null, null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRisposta() {
        return risposta;
    }

    public void setRisposta(String risposta) {
        this.risposta = risposta;
    }

    public Boolean getSelezionata() {
        return selezionata;
    }

    public void setSelezionata(Boolean selezionata) {
        this.selezionata = selezionata;
    }
}
