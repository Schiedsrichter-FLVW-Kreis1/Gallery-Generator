package de.simontenbeitel.srk1.galleriegenerator.model;

public enum League {

    RLWest("Regionalliga", true, 4), OLWFV("Oberliga", true, 5),
    VL("Verbandsliga", true, 6), LL("Landesliga", true, 7), BzL("Bezirksliga", true, 8),
    KLA("Kreisliga A", false, 9), KLB("Kreisliga B", false, 10), KLC("Kreisliga C", false, 11), KLD("Kreisliga D", false, 12);
    public final String description;
    public final boolean isAssociation;
    public int number;
    League(String description, boolean isAssociation, int number) {
        this.description = description;
        this.isAssociation = isAssociation;
        this.number = number;
    }

}
