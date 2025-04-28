package org.example.shkedauth.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "groups")
public class GroupEntity {
    @Id
    private Long id;

    private String name;
}
