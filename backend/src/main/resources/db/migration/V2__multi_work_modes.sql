CREATE TABLE candidate_profile_work_modes (
    profile_id BIGINT NOT NULL,
    work_mode VARCHAR(255) NOT NULL,
    CONSTRAINT pk_candidate_profile_work_modes PRIMARY KEY (profile_id, work_mode),
    CONSTRAINT fk_candidate_profile_work_modes_profile FOREIGN KEY (profile_id) REFERENCES candidate_profiles (id),
    CONSTRAINT ck_candidate_profile_work_modes_value CHECK (work_mode IN ('REMOTE', 'HYBRID', 'ONSITE'))
);

INSERT INTO candidate_profile_work_modes (profile_id, work_mode)
SELECT id, preferred_work_mode
FROM candidate_profiles
WHERE preferred_work_mode IS NOT NULL;

ALTER TABLE candidate_profiles DROP CONSTRAINT ck_candidate_profiles_work_mode;
ALTER TABLE candidate_profiles DROP COLUMN preferred_work_mode;
