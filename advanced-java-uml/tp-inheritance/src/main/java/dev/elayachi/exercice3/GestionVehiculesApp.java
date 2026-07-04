package dev.elayachi.exercice3;

public class GestionVehiculesApp {
    public static void main(String[] args) {
        Vehicule[] vehicules = {
                new Voiture("Dacia Logan", 145000, "Logan Expression", 2023),
                new Moto("Yamaha MT-07", 92000, "Yamaha", 73),
                new Avion("Boeing 737", 850000000, "Royal Air Maroc", 876)
        };

        for (Vehicule vehicule : vehicules) {
            vehicule.emettreSon();
            vehicule.afficherInformations();
            System.out.println("-----------------------------------");
        }
    }
}
