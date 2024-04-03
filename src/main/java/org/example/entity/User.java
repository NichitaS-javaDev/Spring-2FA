package org.example.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String password;
    private String role;
    private boolean mfaEnabled;
    private String secret;

    public User(Long id, String username, String email, String password, String role, boolean mfaEnabled, String secret) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role.toUpperCase();
        this.mfaEnabled = mfaEnabled;
        this.secret = secret;
    }
}
