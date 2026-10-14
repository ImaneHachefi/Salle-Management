package main;

import main.model.Espace;
import main.model.Occupation;
import main.service.EspaceService;
import main.service.impl.EspaceServiceImpl;
import main.util.PageResultat;
import org.h2.tools.Server;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static LocalDateTime t(int heure, int minute) {
        return LocalDateTime.of(2026, 10, 20, heure, minute);
    }

    public static void main(String[] args) throws Exception {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("EspacePU");
        EspaceService service = new EspaceServiceImpl(emf);

        Server console = Server.createWebServer("-web", "-webPort", "8082").start();

        try {
            titre("1. CRÉATION DES ESPACES ET DES OCCUPATIONS");

            String[] noms = {"Atlas", "Majorelle", "Koutoubia", "Bahia", "Menara", "Saadiens",
                    "Medina", "Agdal", "Jemaa", "Ourika", "Essaouira", "Tensift"};
            int[] capacites = {10, 20, 30, 15, 50, 25, 8, 40, 100, 12, 18, 60};

            Map<String, Long> ids = new HashMap<>();
            for (int i = 0; i < noms.length; i++) {
                Espace e = service.creerEspace(noms[i], capacites[i]);
                ids.put(noms[i], e.getId());
            }

            service.reserver(ids.get("Atlas"), t(9, 0), t(11, 0));
            service.reserver(ids.get("Majorelle"), t(10, 0), t(12, 0));
            service.reserver(ids.get("Koutoubia"), t(14, 0), t(16, 0));
            service.reserver(ids.get("Menara"), t(9, 30), t(10, 30));
            service.reserver(ids.get("Jemaa"), t(8, 0), t(18, 0));
            service.reserver(ids.get("Agdal"), t(11, 0), t(13, 0));

            System.out.println(service.listerTous().size() + " espaces créés");
            System.out.println(service.listerOccupations().size() + " occupations créées");

            titre("2. ESPACES DISPONIBLES LE 20/10/2026 DE 10:00 À 12:00 (JPQL)");
            List<Espace> disponibles = service.listerDisponibles(t(10, 0), t(12, 0));
            disponibles.forEach(e -> System.out.println(" - " + e));
            System.out.println("Total : " + disponibles.size());

            titre("3. RECHERCHE MULTI-CRITÈRES (nom contient 'a', capacité >= 15, dispo 10:00-12:00)");
            PageResultat<Espace> p1 = service.rechercher("a", 15, t(10, 0), t(12, 0), 1, 2);
            afficherPage(p1);
            if (p1.aPageSuivante()) {
                afficherPage(service.rechercher("a", 15, t(10, 0), t(12, 0), 2, 2));
            }

            titre("4. PAGINATION DE TOUS LES ESPACES (taille de page = 5)");
            PageResultat<Espace> page = service.listerPagine(1, 5);
            afficherPage(page);
            while (page.aPageSuivante()) {
                page = service.listerPagine(page.getNumeroPage() + 1, 5);
                afficherPage(page);
            }

            titre("5. RÈGLES MÉTIER");
            try {
                service.reserver(ids.get("Atlas"), t(10, 0), t(10, 30));
                System.out.println("ERREUR : le chevauchement n'a pas été détecté");
            } catch (IllegalStateException e) {
                System.out.println("Chevauchement refusé : " + e.getMessage());
            }

            try {
                service.reserver(ids.get("Bahia"), t(15, 0), t(14, 0));
            } catch (IllegalArgumentException e) {
                System.out.println("Dates invalides refusées : " + e.getMessage());
            }

            service.reserver(ids.get("Bahia"), t(13, 0), t(14, 0));
            System.out.println("Réservation valide de Bahia (13:00-14:00) enregistrée");

            titre("6. TOUTES LES OCCUPATIONS");
            for (Occupation o : service.listerOccupations()) {
                System.out.printf(" - %-10s : %s -> %s%n",
                        o.getEspace().getNom(), o.getDebut().format(FMT), o.getFin().format(FMT));
            }

            titre("CONSOLE H2");
            System.out.println("Ouvrez : http://localhost:8082");
            System.out.println("JDBC URL : jdbc:h2:mem:espacesdb;DB_CLOSE_DELAY=-1");
            System.out.println("Utilisateur : sa    Mot de passe : (vide)");
            System.out.println("\nAppuyez sur ENTRÉE pour arrêter l'application...");
            System.in.read();

        } finally {
            console.stop();
            emf.close();
        }
    }

    private static void titre(String texte) {
        System.out.println("\n==================================================");
        System.out.println(texte);
        System.out.println("==================================================");
    }

    private static void afficherPage(PageResultat<Espace> page) {
        System.out.println(page);
        page.getContenu().forEach(e -> System.out.println("   * " + e));
    }
}