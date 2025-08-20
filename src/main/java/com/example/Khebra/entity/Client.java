package com.example.Khebra.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue("Client")
@SuperBuilder

public class Client extends User {

    public String interet;

    @OneToMany(mappedBy = "client", orphanRemoval = true)
    private List<Avis> avis = new ArrayList<>();

}


