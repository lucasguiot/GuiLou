package com.android.app.guilou;

import java.util.Date;

/**
 * Created by Lucas on 30/01/2015.
 */
public class Evenement {
    int eventId;
    String nom;
    String desc;
    Date creationDate;
    Date endDate;
    int creatorId;

    public Evenement(int id, String n, int cId){
        this.eventId = id;
        this.nom = n;
        this.desc = "";
        this.creationDate = null;
        this.endDate = null;
        this.creatorId = cId;
    }

    public Evenement(int id, String n, String d, Date creaD, Date endD, int cId){
        this.eventId = id;
        this.nom = n;
        this.desc = d;
        this.creationDate = creaD;
        this.endDate = endD;
        this.creatorId = cId;
    }

    public int getEventId(){
        return this.eventId;
    }

    public void setEventId(int id){
        this.eventId = id;
    }

    public String getNom(){
        return this.nom;
    }

    public void setNom(String n){
        this.nom = n;
    }

    public String getDesc(){
        return this.desc;
    }

    public void setDesc(String d){
        this.desc = d;
    }

    public Date getCreationDate(){ return this.creationDate;}

    public void setCreationDate(Date d){ this.creationDate = d;}

    public Date getEndDate(){return this.endDate;}

    public void setEndDate(Date d){this.endDate = d;}

    public int getCreatorId(){
        return this.creatorId;
    }

    public void setCreatorId(int cId){
        this.creatorId = cId;
    }
}
