package dev.elayachi.app2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Compte {

    private String numero;
    private LocalDate dateCreation;
    private double solde;
    private Client client;
    private List<Operation> operations = new ArrayList<>();

    public Compte(String numero, LocalDate dateCreation, Client client) {
        this.numero = numero;
        this.dateCreation = dateCreation;
        this.client = client;
        this.solde = 0;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void ajouterOperation(Operation operation) {
        operations.add(operation);
    }

    public double getSolde() {
        solde = 0;
        for (Operation op : operations) {
            if (Operation.VERSEMENT.equals(op.getType())) {
                solde += op.getMontant();
            } else if (Operation.RETRAIT.equals(op.getType())) {
                solde -= op.getMontant();
            }
        }
        return solde;
    }

    public void trierOperationsParDate() {
        operations.sort(Comparator.comparing(Operation::getDate));
    }

    public void afficherDetail() {
        trierOperationsParDate();
        System.out.println("Numero du compte : " + numero);
        System.out.println("Date de creation : " + dateCreation);
        System.out.println("Client : " + client);
        System.out.println("Liste des operations :");
        for (Operation op : operations) {
            System.out.println("  " + op);
        }
        System.out.println("Solde final : " + getSolde());
    }
}
