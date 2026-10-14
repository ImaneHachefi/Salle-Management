package main.service.impl;

import main.model.Espace;
import main.model.Occupation;
import main.repository.EspaceRepository;
import main.repository.OccupationRepository;
import main.service.EspaceService;
import main.util.PageResultat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

public class EspaceServiceImpl implements EspaceService {

    private final EntityManagerFactory emf;

    public EspaceServiceImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Espace creerEspace(String nom, int capacite) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'espace est obligatoire");
        }
        if (capacite <= 0) {
            throw new IllegalArgumentException("La capacité doit être supérieure à 0");
        }
        return enTransaction(em -> new EspaceRepository(em).save(new Espace(nom.trim(), capacite)));
    }

    @Override
    public Occupation reserver(Long espaceId, LocalDateTime debut, LocalDateTime fin) {
        if (debut == null || fin == null || !debut.isBefore(fin)) {
            throw new IllegalArgumentException("Le début doit être strictement avant la fin");
        }
        return enTransaction(em -> {
            EspaceRepository espaceRepo = new EspaceRepository(em);
            OccupationRepository occupationRepo = new OccupationRepository(em);

            Espace espace = espaceRepo.findById(espaceId);
            if (espace == null) {
                throw new IllegalArgumentException("Espace introuvable : id = " + espaceId);
            }
            if (occupationRepo.existeChevauchement(espaceId, debut, fin)) {
                throw new IllegalStateException(
                        "L'espace '" + espace.getNom() + "' est déjà occupé sur ce créneau");
            }
            return occupationRepo.save(new Occupation(espace, debut, fin));
        });
    }


    @Override
    public List<Espace> listerTous() {
        return enLecture(em -> new EspaceRepository(em).findAll());
    }

    @Override
    public List<Espace> listerDisponibles(LocalDateTime debut, LocalDateTime fin) {
        if (debut == null || fin == null || !debut.isBefore(fin)) {
            throw new IllegalArgumentException("Le début doit être strictement avant la fin");
        }
        return enLecture(em -> new EspaceRepository(em).findDisponibles(debut, fin));
    }

    @Override
    public PageResultat<Espace> rechercher(String nom, Integer capaciteMin,
                                           LocalDateTime debut, LocalDateTime fin,
                                           int page, int taille) {
        return enLecture(em -> new EspaceRepository(em)
                .rechercher(nom, capaciteMin, debut, fin, page, taille));
    }

    @Override
    public PageResultat<Espace> listerPagine(int page, int taille) {
        return enLecture(em -> new EspaceRepository(em).findAllPagine(page, taille));
    }

    @Override
    public List<Occupation> listerOccupations() {
        return enLecture(em -> new OccupationRepository(em).findAllAvecEspace());
    }


    private <T> T enTransaction(Function<EntityManager, T> travail) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T resultat = travail.apply(em);
            tx.commit();
            return resultat;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    private <T> T enLecture(Function<EntityManager, T> travail) {
        EntityManager em = emf.createEntityManager();
        try {
            return travail.apply(em);
        } finally {
            em.close();
        }
    }
}