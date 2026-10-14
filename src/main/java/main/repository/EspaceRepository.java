package main.repository;

import main.model.Espace;
import main.model.Occupation;
import main.util.PageResultat;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EspaceRepository {

    private final EntityManager em;

    public EspaceRepository(EntityManager em) {
        this.em = em;
    }

    public Espace save(Espace espace) {
        em.persist(espace);
        return espace;
    }

    public Espace findById(Long id) {
        return em.find(Espace.class, id);
    }

    public List<Espace> findAll() {
        return em.createQuery("SELECT e FROM Espace e ORDER BY e.nom", Espace.class)
                .getResultList();
    }

    // ---------- Étape 5 : JPQL - espaces disponibles sur un créneau ----------
    public List<Espace> findDisponibles(LocalDateTime debut, LocalDateTime fin) {
        String jpql = "SELECT e FROM Espace e WHERE e.id NOT IN ("
                + "SELECT o.espace.id FROM Occupation o "
                + "WHERE o.debut < :fin AND o.fin > :debut"
                + ") ORDER BY e.nom";

        TypedQuery<Espace> query = em.createQuery(jpql, Espace.class);
        query.setParameter("debut", debut);
        query.setParameter("fin", fin);
        return query.getResultList();
    }

    // ---------- Étapes 6 et 7 : Criteria multi-critères + pagination ----------
    public PageResultat<Espace> rechercher(String nomRecherche,
                                           Integer capaciteMin,
                                           LocalDateTime debutRecherche,
                                           LocalDateTime finRecherche,
                                           int numeroPage,
                                           int taillePage) {

        PageResultat.valider(numeroPage, taillePage);
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // Requête principale
        CriteriaQuery<Espace> cq = cb.createQuery(Espace.class);
        Root<Espace> root = cq.from(Espace.class);
        List<Predicate> predicats = construirePredicats(cb, cq, root,
                nomRecherche, capaciteMin, debutRecherche, finRecherche);
        cq.select(root)
                .where(cb.and(predicats.toArray(new Predicate[0])))
                .orderBy(cb.asc(root.get("nom")));

        TypedQuery<Espace> query = em.createQuery(cq);
        query.setFirstResult(PageResultat.calculerDecalage(numeroPage, taillePage));
        query.setMaxResults(taillePage);
        List<Espace> contenu = query.getResultList();

        // Requête de comptage (mêmes filtres)
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Espace> countRoot = countQuery.from(Espace.class);
        List<Predicate> predicatsCount = construirePredicats(cb, countQuery, countRoot,
                nomRecherche, capaciteMin, debutRecherche, finRecherche);
        countQuery.select(cb.count(countRoot))
                .where(cb.and(predicatsCount.toArray(new Predicate[0])));

        Long total = em.createQuery(countQuery).getSingleResult();

        return new PageResultat<>(contenu, numeroPage, taillePage, total);
    }

    public PageResultat<Espace> findAllPagine(int numeroPage, int taillePage) {
        return rechercher(null, null, null, null, numeroPage, taillePage);
    }

    private List<Predicate> construirePredicats(CriteriaBuilder cb,
                                                CriteriaQuery<?> cq,
                                                Root<Espace> root,
                                                String nomRecherche,
                                                Integer capaciteMin,
                                                LocalDateTime debutRecherche,
                                                LocalDateTime finRecherche) {
        List<Predicate> predicats = new ArrayList<>();

        // Critère nom
        if (nomRecherche != null && !nomRecherche.trim().isEmpty()) {
            predicats.add(cb.like(
                    cb.lower(root.<String>get("nom")),
                    "%" + nomRecherche.trim().toLowerCase() + "%"));
        }

        // Critère capacité minimale
        if (capaciteMin != null) {
            predicats.add(cb.greaterThanOrEqualTo(root.<Integer>get("capacite"), capaciteMin));
        }

        // Critère disponibilité
        if (debutRecherche != null && finRecherche != null) {
            Subquery<Long> sousRequete = cq.subquery(Long.class);
            Root<Occupation> occupation = sousRequete.from(Occupation.class);
            sousRequete.select(occupation.<Espace>get("espace").<Long>get("id"));
            sousRequete.where(cb.and(
                    cb.lessThan(occupation.<LocalDateTime>get("debut"), finRecherche),
                    cb.greaterThan(occupation.<LocalDateTime>get("fin"), debutRecherche)));

            predicats.add(cb.not(root.<Long>get("id").in(sousRequete)));
        }

        return predicats;
    }
}