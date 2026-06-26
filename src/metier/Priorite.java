package metier;

public enum Priorite {
    ROUGE(3),
    ORANGE(2),
    VERT(1);

    private final int niveau;

    Priorite(int niveau) {
        this.niveau = niveau;
    }

    public int getNiveau() {
        return niveau;
    }

    public static Priorite fromSeverite(int severite) {
        if (severite >= 8) return ROUGE;
        if (severite >= 4) return ORANGE;
        return VERT;
    }
}
