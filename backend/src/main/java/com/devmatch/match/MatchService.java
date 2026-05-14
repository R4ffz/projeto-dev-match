package com.devmatch.match;

import com.devmatch.job.Job;
import com.devmatch.job.JobMapper;
import com.devmatch.job.JobNotFoundException;
import com.devmatch.job.JobRepository;
import com.devmatch.match.dto.MatchResult;
import com.devmatch.match.dto.RecommendedJob;
import com.devmatch.profile.CandidateProfile;
import com.devmatch.profile.CandidateProfileRepository;
import com.devmatch.profile.ProfileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final JobRepository jobRepository;
    private final CandidateProfileRepository profileRepository;

    @Transactional(readOnly = true)
    public MatchResult calculateMatch(Long userId, Long jobId) {
        CandidateProfile profile = profileRepository.findByUser_Id(userId)
            .orElseThrow(() -> new ProfileNotFoundException(userId));
        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new JobNotFoundException(jobId));
        return MatchCalculator.calculate(profile, job);
    }

    @Transactional(readOnly = true)
    public List<RecommendedJob> recommend(Long userId) {
        CandidateProfile profile = profileRepository.findByUser_Id(userId)
            .orElseThrow(() -> new ProfileNotFoundException(userId));
        return jobRepository.findAll().stream()
            .map(job -> new RecommendedJob(
                JobMapper.toResponse(job),
                MatchCalculator.calculate(profile, job).finalScore()
            ))
            .sorted(Comparator.comparingInt(RecommendedJob::finalScore).reversed()
                .thenComparing(r -> r.job().id()))
            .toList();
    }
}
