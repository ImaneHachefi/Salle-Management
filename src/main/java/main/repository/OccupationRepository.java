package main.repository;

import main.model.Occupation;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class OccupationRepository {

    private final EntityManager em;

    public OccupationRepository(EntityManager em) {
        this.em = em;
    }

    public Occupation save(Occupation occupation) {
        em.persist(occupation);
        return occupation;
    }

    public List<Occupation> findAllAvecEspace() {
        return em.createQuery(
                "SELECT o FROM Occupation o JOIN FETCH o.espace e ORDER BY o.debut, e.nom",
                Occupation.class).getResultList();
    }

    public boolean existeChevauchement(Long espaceId, LocalDateTime debut, LocalDateTime fin) {
        Long nb = em.createQuery(
                        "SELECT COUNT(o) FROM Occupation o "
                                + "WHERE o.espace.id = :id AND o.debut < :fin AND o.fin > :debut",
                        Long.class)
                .setParameter("id", espaceId)
                .setParameter("debut", debut)
                .setParameter("fin", fin)
                .getSingleResult();
        return nb > 0;
    }
}