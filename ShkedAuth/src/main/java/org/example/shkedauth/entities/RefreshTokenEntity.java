package org.example.shkedauth.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class RefreshTokenEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    @Column(unique = true)
    private String token;

    private Date expiryDate;
}
