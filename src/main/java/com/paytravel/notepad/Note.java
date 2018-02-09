package com.paytravel.notepad;

import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Вадим on 09.02.2018.
 */
public class Note {
    public static final String DATETIME_PATTERN = "yyyy-MM-dd-HH.mm.ss";

    private LocalDateTime date;
    private final SimpleStringProperty note;
    private final SimpleStringProperty dateString;

    public Note(LocalDateTime date, String note) {
        this.date = date;
        this.dateString = new SimpleStringProperty(getLocalDateTimeString(date));
        this.note = new SimpleStringProperty(note);
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDateString() {
        return dateString.get();
    }

    public void setDateString(String dateString) {
        this.dateString.set(dateString);
    }

    public String getNote() {
        return note.get();
    }

    public void setNote(String note) {
        this.note.set(note);
    }

    public static String getLocalDateTimeString(LocalDateTime nowDateTime) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
        return nowDateTime.format(dateFormat);
    }
}
