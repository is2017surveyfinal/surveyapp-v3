package it.survey.survey_is.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simone on 17/07/2017.
 */

public class Sondaggio implements Serializable {

    private String id;
    private String title;
    private List<Domanda> domande;

    public Sondaggio(){
        this(null, null, new ArrayList<Domanda>());
    }

    public Sondaggio(String id, String title, List<Domanda> domande){
        this.id=id;
        this.title=title;
        this.domande=domande;
    }

    public void setId (String id){
        this.id=id;
    }
    public void setTitle (String title){
        this.title=title;
    }

    public String getId (){
        return id;
    }
    public String getTitle(){
        return title;
    }

    public List<Domanda> getDomande() {
        return domande;
    }

    public void setDomande(List<Domanda> domande) {
        this.domande = domande;
    }
}
