package org.example.shked.Groups.DAL.repositories;

import org.example.shked.Groups.DAL.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GroupsRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByName(String name);
    List<Group> findByUpdatedAtAfter(LocalDateTime cachedTime);
}
