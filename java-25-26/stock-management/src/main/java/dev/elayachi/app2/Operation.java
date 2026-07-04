package dev.elayachi.app2;

import java.time.LocalDate;

public class Operation {

    public static final String VERSEMENT = "VERS";
    public static final String RETRAIT = "RETR";

    private LocalDate date;
    private double montant;
    private String type;

    public Operation(LocalDate date, double montant, String type) {
        this.date = date;
        this.montant = montant;
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Operation{date=" + date
                + ", montant=" + montant
                + ", type='" + type + '\'' + '}';
    }
}
