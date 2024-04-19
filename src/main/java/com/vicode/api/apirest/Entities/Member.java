package com.vicode.api.apirest.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Date; // Importar la clase Date

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private Long memberNumber;
    @Column(columnDefinition = "TEXT")
    private String imageBase64;
    private Date dateOfBirth; // Agregar campo para fecha de nacimiento
    private String address; // Agregar campo para dirección
    private String position; // Agregar campo para cargo
    private String activity; // Agregar campo para actividad
    private Date dateOfJoiningChurch; // Agregar campo para fecha de ingreso a la iglesia
    private Date dateOfBaptism; // Agregar campo para fecha de bautismo
    private String status; // Agregar campo para estado

    // Getters y setters para los campos nuevos
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public Date getDateOfJoiningChurch() {
        return dateOfJoiningChurch;
    }

    public void setDateOfJoiningChurch(Date dateOfJoiningChurch) {
        this.dateOfJoiningChurch = dateOfJoiningChurch;
    }

    public Date getDateOfBaptism() {
        return dateOfBaptism;
    }

    public void setDateOfBaptism(Date dateOfBaptism) {
        this.dateOfBaptism = dateOfBaptism;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getters y setters para los campos existentes
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(Long memberNumber) {
        this.memberNumber = memberNumber;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}
