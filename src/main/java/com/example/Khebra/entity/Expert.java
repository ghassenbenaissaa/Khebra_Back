package com.example.Khebra.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue("Expert")
@SuperBuilder

public class Expert extends User {

    public String expertise;
    public String biographie;
    public Double rating;
    public boolean isValidated;

    @ManyToOne
    @JoinColumn(name = "domaine_id")
    private Domaine domaine;


    @OneToMany(mappedBy = "expert", orphanRemoval = true)
    private List<Avis> avis = new ArrayList<>();

}
