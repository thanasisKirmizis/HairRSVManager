package com.kirman.hairrsvmanagerlocal;

import java.util.Calendar;

public class clientEntity {

    private int clientID;
    private String name;
    private String phone;
    private int timesFree;
    private String note;
    private Calendar firstAdded;
    private Calendar lastCame;

    public clientEntity(){

        this.timesFree = 1;
    }

    public clientEntity(String name, String phone, Calendar firstAdded) {
        this.name = name;
        this.phone = phone;
        this.timesFree = 1;
        this.firstAdded = firstAdded;
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getTimesFree() {
        return timesFree;
    }

    public void setTimesFree(int timesFree) {
        this.timesFree = timesFree;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Calendar getFirstAdded() {
        return firstAdded;
    }

    public void setFirstAdded(Calendar firstAdded) {
        this.firstAdded = firstAdded;
    }

    public Calendar getLastCame() {
        return lastCame;
    }

    public void setLastCame(Calendar lastCame) {
        this.lastCame = lastCame;
    }
}
