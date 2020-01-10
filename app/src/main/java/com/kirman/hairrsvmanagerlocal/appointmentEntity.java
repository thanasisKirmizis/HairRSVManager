package com.kirman.hairrsvmanagerlocal;

import java.util.Calendar;

public class appointmentEntity {

    private int appointID;
    private clientEntity client;
    private Calendar date;
    private String note;
    private boolean isPast;

    public appointmentEntity() {

        //Lonely in here...
    }

    public appointmentEntity(clientEntity client, Calendar date) {
        this.client = client;
        this.date = date;
    }

    public int getAppointID() {
        return appointID;
    }

    public void setAppointID(int appointID) {
        this.appointID = appointID;
    }

    public clientEntity getClient() {
        return client;
    }

    public void setClient(clientEntity client) {
        this.client = client;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setIsPast(boolean isPast) {
        this.isPast = isPast;
    }

    public boolean getIsPast() {
        return isPast;
    }
}
