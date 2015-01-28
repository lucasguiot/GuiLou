package com.android.app.guilou;

/**
 * Created by Lucas on 26/01/2015.
 */
public class User {
    public int id;
    public String login;

    public User(int i, String l){
        this.id = i;
        this.login = l;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int i){
        this.id = i;
    }

    public String getLogin(){
        return this.login;
    }

    public void setString(String l){
        this.login = l;
    }
}
