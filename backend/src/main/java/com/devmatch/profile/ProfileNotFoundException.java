package com.devmatch.profile;

public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(Long userId) {
        super("Candidate profile not found for user id " + userId);
    }
}
