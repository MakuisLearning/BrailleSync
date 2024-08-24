package com.example.braille_sync.Classes;

import com.google.firebase.Timestamp;

public class History {
    private String text;
    private Timestamp time;


    private String documentID;

    public History() {}

    public History(String text, Timestamp time) {
        this.text = text;
        this.time = time;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
