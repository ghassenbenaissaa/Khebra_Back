package com.example.Khebra.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="_user")
@EntityListeners(AuditingEntityListener.class)
@DiscriminatorColumn(name = "user_type")
@Inheritance(strategy=InheritanceType.JOINED)
@SuperBuilder
public abstract class User implements UserDetails, Principal {

    @Id
    @GeneratedValue
    private Integer id;
    private String firstname;
    private String lastname;
    @Column(unique=true)
    private String email;
    private String password;
    private String numTel;
    private String adresse;
    @Column(unique=true)
    private String cin;
    private Boolean IsActive;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDate createdDate;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDate lastModifiedDate;
    @Column(nullable = false)
    private boolean isBanned = false;

    @Column
    private String point;



    @OneToMany(mappedBy = "expert", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DemandeCommunication> demandeCommunications_expert = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DemandeCommunication> demandesEnvoyees = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Image image;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this instanceof Expert) {
            return List.of(new SimpleGrantedAuthority("ROLE_EXPERT"));
        } else if (this instanceof Client) {
            return List.of(new SimpleGrantedAuthority("ROLE_CLIENT"));
        } else if (this instanceof Admin) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return List.of();
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(this.IsActive);
    }

    @Override
    public String getName() {
        return email;
    }

    public String getFullName(){
        return firstname + " " + lastname;
    }



}
