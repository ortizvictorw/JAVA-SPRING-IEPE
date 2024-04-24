package com.vicode.api.apirest.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Date;

@Entity
public class ListEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String day;
    private String keyboard1;
    private String keyboard2;
    private String guitar1;
    private String guitar2;
    private String bass;
    private String drums;

    @Column(nullable = false)
    private Date date;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getKeyboard1() {
        return keyboard1;
    }

    public void setKeyboard1(String keyboard1) {
        this.keyboard1 = keyboard1;
    }

    public String getKeyboard2() {
        return keyboard2;
    }

    public void setKeyboard2(String keyboard2) {
        this.keyboard2 = keyboard2;
    }

    public String getGuitar1() {
        return guitar1;
    }

    public void setGuitar1(String guitar1) {
        this.guitar1 = guitar1;
    }

    public String getGuitar2() {
        return guitar2;
    }

    public void setGuitar2(String guitar2) {
        this.guitar2 = guitar2;
    }

    public String getBass() {
        return bass;
    }

    public void setBass(String bass) {
        this.bass = bass;
    }

    public String getDrums() {
        return drums;
    }

    public void setDrums(String drums) {
        this.drums = drums;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
