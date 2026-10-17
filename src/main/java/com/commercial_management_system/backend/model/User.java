package com.commercial_management_system.backend.model;

import com.commercial_management_system.backend.enums.UserStatus;
import com.commercial_management_system.backend.enums.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "telephone", nullable = false, unique = true)
    private String telephone;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus;


    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    @Version
    private Long version;

    public User(String name, String email, String telephone, String password, UserType userType, UserStatus userStatus) {
        this.name = name;
        this.email = email;
        this.telephone = telephone;
        this.password = password;
        this.userType = userType;
        this.userStatus = userStatus;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // mapeia role dinamicamente conforme o tipo do usuario
        if (userType != null) {
            return List.of(new SimpleGrantedAuthority(userType.name()));
        }
        return List.of(new SimpleGrantedAuthority(UserType.ROLE_ADMIN.name()));
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // usuario ativo possui autenticacao habilitada
        return userStatus == UserStatus.ACTIVE;
    }
}
