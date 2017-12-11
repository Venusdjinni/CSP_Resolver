package com.venus.java;

/**
 * Created by arnold on 08/12/17.
 */
public class Cours {
    private static int id = 1;
    private int idCours;
    private String nom;
    private int nbEleves;
    private boolean[][] disponiblite;

    public Cours(String nom, int nbEleves, boolean[][] disponiblite) {
        idCours = id;
        this.nom = nom;
        this.nbEleves = nbEleves;
        this.disponiblite = disponiblite;
        id++;
    }

    public int getNbDisponibilites() {
        int count = 0;
        for (boolean[] bt : disponiblite)
            for (boolean b : bt)
                if (b) count+=1;
        return count;
    }

    public int getIdCours() {
        return idCours;
    }

    public String getNom() {
        return nom;
    }

    @Override
    public String toString() {
        String s = nom + ", " + nbEleves + "\n";
        for (int i = 0; i < disponiblite.length; i++) {
            for (int j = 0; j < disponiblite[i].length; j++)
                s+= disponiblite[i][j] + " ";
            s += "\n";
        }
        return s;
    }

    public int getNbEleves() {
        return nbEleves;
    }

    public void setNbEleves(int nbEleves) {
        this.nbEleves = nbEleves;
    }

    public boolean[][] getDisponiblite() {
        return disponiblite;
    }

    public void setDisponiblite(boolean[][] disponiblite) {
        this.disponiblite = disponiblite;
    }
}
