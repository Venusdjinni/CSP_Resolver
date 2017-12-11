package com.venus.java;

/**
 * Created by arnold on 08/12/17.
 */
public class Salle {
    private static int id = 1;
    private int idSalle;
    private String nom;
    private int capacite;

    public Salle(String nom, int capacite) {
        idSalle = id;
        this.nom = nom;
        this.capacite = capacite;
        id++;
    }

    public int getIdSalle() {
        return idSalle;
    }

    public String getNom() {
        return nom;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }
}
