package org.example.shkedauth.repositories;

import org.example.shkedauth.entities.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

}