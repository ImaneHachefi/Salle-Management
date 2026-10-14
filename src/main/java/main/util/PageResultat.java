package main.util;

import java.util.Collections;
import java.util.List;

/**
 * Classe utilitaire de pagination : contient le contenu d'une page
 * ainsi que les informations de navigation.
 */
public class PageResultat<T> {

    private final List<T> contenu;
    private final int numeroPage;
    private final int taillePage;
    private final long totalElements;
    private final int totalPages;

    public PageResultat(List<T> contenu, int numeroPage, int taillePage, long totalElements) {
        this.contenu = Collections.unmodifiableList(contenu);
        this.numeroPage = numeroPage;
        this.taillePage = taillePage;
        this.totalElements = totalElements;
        this.totalPages = calculerTotalPages(totalElements, taillePage);
    }

    public static int calculerTotalPages(long totalElements, int taillePage) {
        return (int) Math.ceil((double) totalElements / taillePage);
    }

    public static int calculerDecalage(int numeroPage, int taillePage) {
        return (numeroPage - 1) * taillePage;
    }

    public static void valider(int numeroPage, int taillePage) {
        if (numeroPage < 1) {
            throw new IllegalArgumentException("Le numéro de page doit être >= 1");
        }
        if (taillePage < 1) {
            throw new IllegalArgumentException("La taille de page doit être >= 1");
        }
    }

    public List<T> getContenu() { return contenu; }
    public int getNumeroPage() { return numeroPage; }
    public int getTaillePage() { return taillePage; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }

    public boolean aPageSuivante() { return numeroPage < totalPages; }
    public boolean aPagePrecedente() { return numeroPage > 1; }

    @Override
    public String toString() {
        return "Page " + numeroPage + "/" + totalPages
                + " (" + contenu.size() + " élément(s), total = " + totalElements + ")";
    }
}