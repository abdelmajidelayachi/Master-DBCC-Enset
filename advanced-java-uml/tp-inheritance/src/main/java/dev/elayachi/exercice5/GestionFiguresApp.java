package dev.elayachi.exercice5;

public class GestionFiguresApp {
    public static void main(String[] args) {
        Figure[] figures = {
                new Cercle("Cercle C1", 5),
                new Rectangle("Rectangle R1", 8, 4),
                new Triangle("Triangle T1", 3, 4, 5)
        };

        for (Figure figure : figures) {
            figure.afficherDetails();
        }
    }
}
