package com.venus.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;

import static com.venus.java.MainClass.SALLES;

/**
 * Created by arnold on 10/12/17.
 */
public class CSP {
    private ArrayList<Cours> X;
    private HashMap<Integer, ValeurX[]> D;

    public CSP(Collection<Cours> cours) {
        X = new ArrayList<>(cours);
        X.sort(Comparator.comparingInt(Cours::getNbDisponibilites));
        D = new HashMap<>();
        construitDomaines();
    }

    private void construitDomaines() {
        for (Cours c : X) {
            ArrayList<ValeurX> a = new ArrayList<>();
            for (Salle s : SALLES.values())
                for (int i = 0; i < 5; i++)
                    for (int j = 0; j < 4; j++)
                        if (C(c, new ValeurX(s.getIdSalle(), new Couple(i, j))))
                            a.add(new ValeurX(s.getIdSalle(), new Couple(i, j)));
            D.put(c.getIdCours(), a.toArray(new ValeurX[]{}));
        }
    }

    public boolean C(Cours c, ValeurX v) {
        return c.getDisponiblite()[v.pos.x][v.pos.y] &&
                c.getNbEleves() <= SALLES.get(v.salle).getCapacite();
    }

    public ArrayList<Cours> getX() {
        return X;
    }

    public HashMap<Integer, ValeurX[]> getD() {
        return D;
    }

    public void setD(HashMap<Integer, ValeurX[]> d) {
        D = d;
    }

    public class ValeurX {
        private int salle;
        private Couple pos;

        public ValeurX(int salle, Couple pos) {
            this.salle = salle;
            this.pos = pos;
        }

        public int getSalle() {
            return salle;
        }

        public Couple getPos() {
            return pos;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof ValeurX) && this.salle == ((ValeurX) o).salle && this.pos.equals(((ValeurX) o).pos);
        }

        @Override
        public String toString() {
            return "idSalle = " + salle + ", pos = " + pos;
        }
    }
}
