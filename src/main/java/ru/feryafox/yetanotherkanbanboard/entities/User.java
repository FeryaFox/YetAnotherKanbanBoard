package ru.feryafox.yetanotherkanbanboard.entities;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.Column(unique = true, nullable = false)
    private String username;

    @jakarta.persistence.Column(nullable = false)
    private String password;

    @jakarta.persistence.Column(nullable = false)
    private String firstName;

    @jakarta.persistence.Column(nullable = false)
    private String surname;

    @jakarta.persistence.Column(nullable = false)
    private String middleName;

    private String roles = "ROLE_USER";

    private boolean isEnabled = true;
    private boolean isAccountNonExpired = true;
    private boolean isAccountNonLocked = true;
    private boolean isCredentialsNonExpired = true;

    @OneToMany(mappedBy = "userOwner", cascade = CascadeType.ALL)
    Set<Card> cardsOwned;

    @OneToMany(mappedBy = "boardOwner", orphanRemoval = true)
    private Set<Board> boardsOwned = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "users_accessible_boards",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "boards_id"))
    private Set<Board> boardsAccessible = new LinkedHashSet<>();

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Column> createdColumns = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "cards_responsible_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "card_id"))
    private Set<Card> cardsResponsibled = new LinkedHashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.commaSeparatedStringToAuthorityList(roles);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}

