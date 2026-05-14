package com.devmatch.job;

import com.devmatch.job.dto.JobFilter;
import com.devmatch.job.dto.JobResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    @Transactional(readOnly = true)
    public List<JobResponse> search(JobFilter filter) {
        Specification<Job> spec = JobSpecifications.build(filter);
        return jobRepository.findAll(spec).stream()
            .sorted(Comparator.comparing(Job::getId))
            .map(JobMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public JobResponse findById(Long id) {
        Job job = jobRepository.findById(id)
            .orElseThrow(() -> new JobNotFoundException(id));
        return JobMapper.toResponse(job);
    }
}
