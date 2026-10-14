package main.service;

import main.model.Espace;
import main.model.Occupation;
import main.util.PageResultat;

import java.time.LocalDateTime;
import java.util.List;

public interface EspaceService {

    Espace creerEspace(String nom, int capacite);

    Occupation reserver(Long espaceId, LocalDateTime debut, LocalDateTime fin);

    List<Espace> listerTous();

    List<Espace> listerDisponibles(LocalDateTime debut, LocalDateTime fin);

    PageResultat<Espace> rechercher(String nom, Integer capaciteMin,
                                    LocalDateTime debut, LocalDateTime fin,
                                    int page, int taille);

    PageResultat<Espace> listerPagine(int page, int taille);

    List<Occupation> listerOccupations();
}