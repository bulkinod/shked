package org.example.shkedauth.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    private String id;
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    private String groupName;
}