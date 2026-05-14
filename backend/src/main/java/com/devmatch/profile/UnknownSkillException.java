package com.devmatch.profile;

import lombok.Getter;

import java.util.Set;

@Getter
public class UnknownSkillException extends RuntimeException {

    private final Set<String> unknownSkills;

    public UnknownSkillException(Set<String> unknownSkills) {
        super("Unknown skill(s): " + String.join(", ", unknownSkills));
        this.unknownSkills = unknownSkills;
    }
}
