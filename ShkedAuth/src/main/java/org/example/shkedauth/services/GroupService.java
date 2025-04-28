package org.example.shkedauth.services;

import org.example.shkedauth.entities.GroupEntity;
import org.example.shkedauth.exceptions.InvalidGroupException;
import org.example.shkedauth.repositories.GroupRepository;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public String validateAndGetNormalizedGroupName(String inputGroupName) {
        String normalizedInput = normalizeGroupName(inputGroupName);

        return groupRepository.findAll().stream()
                .map(GroupEntity::getName)
                .filter(name -> normalizeGroupName(name).equals(normalizedInput)) 
                .findFirst()
                .orElseThrow(() -> new InvalidGroupException("Group name is invalid"));
    }

    private String normalizeGroupName(String input) {
        if (input == null) return null;
        return input
                .toLowerCase()
                .replaceAll("[^а-яa-z0-9-]", "");
    }
}
