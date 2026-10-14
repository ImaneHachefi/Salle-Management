package main.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "espaces")
public class Espace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(nullable = false)
    private int capacite;

    @OneToMany(mappedBy = "espace", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Occupation> occupations = new HashSet<>();

    public Espace() {
    }

    public Espace(String nom, int capacite) {
        this.nom = nom;
        this.capacite = capacite;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }

    public Set<Occupation> getOccupations() { return occupations; }
    public void setOccupations(Set<Occupation> occupations) { this.occupations = occupations; }

    public void ajouterOccupation(Occupation occupation) {
        occupations.add(occupation);
        occupation.setEspace(this);
    }

    public void retirerOccupation(Occupation occupation) {
        occupations.remove(occupation);
        occupation.setEspace(null);
    }

    @Override
    public String toString() {
        return "Espace{id=" + id + ", nom='" + nom + "', capacite=" + capacite + "}";
    }
}