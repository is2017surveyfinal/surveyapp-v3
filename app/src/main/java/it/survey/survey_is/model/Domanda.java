package it.survey.survey_is.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Domanda implements Serializable {
    private String id;
    private String argomento;
    private String testo;
    private String immagine;
    private List<Risposta> risposte;

    public Domanda(String id, String argomento, String testo, String immagine, List<Risposta> risposte) {
        this.argomento = argomento;
        this.testo = testo;
        this.immagine = immagine;
        this.risposte = risposte;
    }

    public Domanda() {
        this(null, null, null, null, new ArrayList<Risposta>());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArgomento() {
        return argomento;
    }

    public void setArgomento(String argomento) {
        this.argomento = argomento;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public List<Risposta> getRisposte() {
        return risposte;
    }

    public void setRisposte(List<Risposta> risposte) {
        this.risposte = risposte;
    }
}
