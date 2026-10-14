package main.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "occupations")
public class Occupation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espace_id", nullable = false)
    private Espace espace;

    @Column(nullable = false)
    private LocalDateTime debut;

    @Column(nullable = false)
    private LocalDateTime fin;

    public Occupation() {
    }

    public Occupation(Espace espace, LocalDateTime debut, LocalDateTime fin) {
        this.espace = espace;
        this.debut = debut;
        this.fin = fin;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Espace getEspace() { return espace; }
    public void setEspace(Espace espace) { this.espace = espace; }

    public LocalDateTime getDebut() { return debut; }
    public void setDebut(LocalDateTime debut) { this.debut = debut; }

    public LocalDateTime getFin() { return fin; }
    public void setFin(LocalDateTime fin) { this.fin = fin; }

    @Override
    public String toString() {
        return "Occupation{id=" + id + ", debut=" + debut + ", fin=" + fin + "}";
    }
}