package ru.feryafox.yetanotherkanbanboard.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "cards")
@Data
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.Column(name = "title")
    private String title;

    @jakarta.persistence.Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "column_id", nullable = false)
    private Column column;

    @ManyToOne
    @JoinColumn(name = "user_owner_id")
    private User userOwner;

    @ManyToMany
    @JoinTable(name = "cards_responsible_user",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> userResponsible = new LinkedHashSet<>();

}
