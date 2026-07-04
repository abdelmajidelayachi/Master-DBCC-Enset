package dev.elayachi.app1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Departement {

    private int id;
    private String nom;
    private List<Salarie> salaries = new ArrayList<>();

    public Departement(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Salarie> getSalaries() {
        return salaries;
    }

    public void ajouterSalarie(Salarie salarie) {
        salaries.add(salarie);
        salarie.setDepartement(this);
    }

    public double totalSalaires() {
        double total = 0;
        for (Salarie s : salaries) {
            total += s.getSalaire();
        }
        return total;
    }

    public void trierParSalaire() {
        salaries.sort(Comparator.comparingDouble(Salarie::getSalaire));
    }

    public void afficherDetail() {
        trierParSalaire();
        System.out.println("Departement : " + nom + " (id=" + id + ")");
        System.out.println("Liste des salaries :");
        for (Salarie s : salaries) {
            System.out.println("  " + s);
        }
        System.out.println("Total des salaires : " + totalSalaires());
    }
}
