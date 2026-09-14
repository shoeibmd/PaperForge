package com.paperforge.repository;

import com.paperforge.model.Job;
import com.paperforge.model.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {
    List<Job> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Job> findByStatusAndExpiresAtBefore(JobStatus status, LocalDateTime now);
    long countByStatus(JobStatus status);
}
