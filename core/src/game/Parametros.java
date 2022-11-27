package game;

public class Parametros {

    public static int vidaTotalMax = 10;
    public static int anchoCarta = 190;
    public static int altoCarta = 280;
    public static int score = 0;
    private static int anchoPantalla = 1600;
    private static int altoPantalla = 1000;

    public static int getAnchoPantalla() {
        return anchoPantalla;
    }

    public static void setAnchoPantalla(int anchoPantalla) {
        Parametros.anchoPantalla = anchoPantalla;
    }

    public static int getAltoPantalla() {
        return altoPantalla;
    }

    public static void setAltoPantalla(int altoPantalla) {
        Parametros.altoPantalla = altoPantalla;
    }

    public static int getVidaTotalMax() {
        return vidaTotalMax;
    }

    public static void setVidaTotalMax(int vidaTotalMax) {
        Parametros.vidaTotalMax = vidaTotalMax;
    }

    public static int getAnchoCarta() {
        return anchoCarta;
    }

    public static void setAnchoCarta(int anchoCarta) {
        Parametros.anchoCarta = anchoCarta;
    }

    public static int getAltoCarta() {
        return altoCarta;
    }

    public static void setAltoCarta(int altoCarta) {
        Parametros.altoCarta = altoCarta;
    }

    public static int getScore() {
        return score;
    }

    public static void setScore(int score) {
        Parametros.score = score;
    }
}