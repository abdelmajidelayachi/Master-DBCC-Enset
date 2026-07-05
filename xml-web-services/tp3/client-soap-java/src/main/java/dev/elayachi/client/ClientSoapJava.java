package dev.elayachi.client;

import dev.elayachi.client.stubs.BanqueService;
import dev.elayachi.client.stubs.BanqueWS;
import dev.elayachi.client.stubs.Compte;

import java.util.List;

public class ClientSoapJava {
    public static void main(String[] args) {
        // Le Stub (proxy) se charge de la communication SOAP avec le serveur
        BanqueService proxy = new BanqueWS().getBanqueServicePort();

        System.out.println("=== conversion Euro -> DH ===");
        double montantDH = proxy.conversionEuroToDH(100);
        System.out.println("100 EUR = " + montantDH + " DH");

        System.out.println("=== getCompte ===");
        Compte compte = proxy.getCompte(5);
        System.out.println("Code  : " + compte.getCode());
        System.out.println("Solde : " + compte.getSolde());

        System.out.println("=== getComptes ===");
        List<Compte> comptes = proxy.getComptes();
        for (Compte c : comptes) {
            System.out.println("Compte " + c.getCode() + " | solde = " + c.getSolde());
        }
    }
}
