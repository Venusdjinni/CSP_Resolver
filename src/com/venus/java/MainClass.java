package com.venus.java;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by arnold on 08/12/17.
 */
public class MainClass {
    static HashMap<Integer, Cours> COURS = new HashMap<>();
    static HashMap<Integer, Salle> SALLES = new HashMap<>();

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Arguments incorects:\nEntrez le chemin du fichier des cours et de celui des salles");
            return;
        } else try {
            readDatas(args[0], args[1]);
        } catch (Exception e) {
            System.out.println("Erreur de lecture des fichiers");
            return;
        }

        CSP csp = new CSP(COURS.values());
        HashMap<Integer, CSP.ValeurX> A = new HashMap<>();

        if (testePuisEnumere(csp, A)) {
            System.out.println("Emploi du temps:");
            print(A);
        } else System.out.println("Impossible de générer un emploi du temps correspondant");
    }

    private static boolean testePuisEnumere(CSP csp, HashMap<Integer, CSP.ValeurX> A) {
        /* 1 : Toutes les variables affectées */
        if (csp.getX().isEmpty()) return true;
        else {
            /* 2 : Choix de Xi */
            Cours Xi = csp.getX().get(0);
            csp.getX().remove(0);
            /* 3 : Pour Vi de D(Xi) */
            for (CSP.ValeurX Vi : csp.getD().get(Xi.getIdCours())) {
                HashMap<Integer, CSP.ValeurX[]> DFiltre = new HashMap<>(csp.getD());
                /* 4 : Pour Xj non affecté */
                for (Cours Xj : csp.getX()) {
                    ArrayList<CSP.ValeurX> DfiltreXj = new ArrayList<>();
                    /* 5 : Filtrage */
                    for (CSP.ValeurX Vj : csp.getD().get(Xj.getIdCours())) {
                        if (estConsistante(csp, A, Xi, Vi, Xj, Vj))
                            DfiltreXj.add(Vj);
                    }
                    DFiltre.put(Xj.getIdCours(), DfiltreXj.toArray(new CSP.ValeurX[]{}));
                    /* 6 : DFiltre vide */
                    if (DfiltreXj.isEmpty()) return false;
                }
                /* 7 : testeEnumere() = vrai */
                A.put(Xi.getIdCours(), Vi);
                CSP cspFiltre = new CSP(csp.getX());
                cspFiltre.setD(DFiltre);
                if (testePuisEnumere(cspFiltre, A))
                    return true;
            }
            return false;
        }
    }

    private static boolean estConsistante(CSP csp, HashMap<Integer, CSP.ValeurX> Ap, Cours Xi, CSP.ValeurX Vi, Cours Xj, CSP.ValeurX Vj) {
        boolean b1 = false, b2 = false, b3 = true;
        if (Ap.get(Xi.getIdCours()) == null || Ap.get(Xi.getIdCours()).getSalle() != Vi.getSalle()) b1 = true;
        if (Ap.get(Xj.getIdCours()) == null || Ap.get(Xj.getIdCours()).getSalle() != Vj.getSalle()) b2 = true;
        if (Vi.equals(Vj)) b3 = false;

        return b1 && b2 && b3 && csp.C(Xi, Vi) && csp.C(Xj, Vj);
    }

    private static void readDatas(String srcCours, String srcSalles) throws FileNotFoundException {
        // lit les donnees des fichiers et les insere dans les listes COURS et SALLES

        String cours = convertStreamToString(new BufferedInputStream(new FileInputStream(srcCours)));

        String[] courss = cours.split("\n\n");
        for (String str : courss) {
            String[] str2 = str.split("\n");
            boolean[][] btt = new boolean[5][4];
            for (int i = 2; i < 7; i++) {
                boolean[] bt = new boolean[4];
                for (int j = 0; j < str2[i].length(); j++)
                    bt[j] = str2[i].charAt(j) != '0';
                btt[i - 2] = bt;
            }
            Cours c = new Cours(str2[0], Integer.parseInt(str2[1]), btt);
            COURS.put(c.getIdCours(), c);
        }

        String salles = convertStreamToString(new BufferedInputStream(new FileInputStream(srcSalles)));
        int index = 0;
        while (salles.substring(index).indexOf('\n') != -1) {
            String ligne = salles.substring(index, index + salles.substring(index).indexOf('\n'));

            if (!ligne.isEmpty()) {
                Salle s = new Salle(ligne.substring(0, ligne.lastIndexOf(' ')), Integer.parseInt(ligne.substring(ligne.lastIndexOf(' ') + 1)));
                SALLES.put(s.getIdSalle(), s);
            }

            index += salles.substring(index).indexOf('\n') + 1;
        }
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private static void print(HashMap<Integer, CSP.ValeurX> A) {
        // On commence par chercher le nom de cours le plus long
        int maxCours = 0, maxSalles = 0;
        for (Cours c : COURS.values())
            if (c.getNom().length() > maxCours)
                maxCours = c.getNom().length();
        for (Salle s : SALLES.values())
            if (s.getNom().length() > maxSalles)
                maxSalles = s.getNom().length();
        // On cherche maintenant combien de cours au maximum se font dans la meme salle
        int maxMSalle = 0;
        int[][] counts = new int[5][4];
        for (CSP.ValeurX v : A.values())
            counts[v.getPos().x][v.getPos().y]++;
        for (int[] ii : counts)
            for (int i : ii)
                if (i > maxMSalle)
                    maxMSalle = i;
        // Et on construit le tableau
        int maxLargeur = maxCours + maxSalles + 2;
        for (int i = 0; i < 5 + maxLargeur * 4; i++) System.out.print("-"); System.out.println();
        for (int i = 0; i < 5; i++) {
            int maxLocal = 0;
            for (int l = 0; l < 4; l++)
                if (counts[i][l] > maxLocal)
                    maxLocal = counts[i][l];
            if (maxLocal == 0) {
                for (int l = 0; l < 4; l++) {
                    System.out.print("|");
                    for (int k = 0; k < maxLargeur; k++) System.out.print(" ");
                }
                System.out.println("|");
            } else while (maxLocal > 0) {
                for (int j = 0; j < 4; j++) {
                    System.out.print("|");
                    int longeur = 0;
                    for (Map.Entry<Integer, CSP.ValeurX> e : A.entrySet()) {
                        if (new Couple(i, j).equals(e.getValue().getPos())) {
                            String s = COURS.get(e.getKey()).getNom() + ", " + SALLES.get(e.getValue().getSalle()).getNom();
                            A.remove(e.getKey());
                            longeur = s.length();
                            System.out.print(s);
                            break;
                        }
                    }
                    // on complete les espaces et les '|'
                    if (longeur > 0) { // on a ecrit un cours
                        for (int k = 0; k < maxLargeur - longeur; k++) System.out.print(" ");
                    } else {
                        for (int k = 0; k < maxLargeur; k++) System.out.print(" ");
                    }
                }
                System.out.println("|");
                maxLocal--;
            }
            for (int k = 0; k < 5 + maxLargeur * 4; k++) System.out.print("-"); System.out.println();
        }



        /*for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                boolean flag = false;
                for (Map.Entry<Integer, CSP.ValeurX> e : A.entrySet()) {
                    if (new Couple(i, j).equals(e.getValue().getPos())) {
                        System.out.print(e.getKey() + "," + e.getValue().getSalle() + " | ");
                        flag = true;
                    }
                }
                if (!flag) System.out.print("0,0");
                System.out.print("   ");
            }
            System.out.println();
        }*/
    }
}
