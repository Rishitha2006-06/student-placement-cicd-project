package com.example.placement;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface JobApplicationRepository extends JpaRepository<JobApplication,Long> {
 List<JobApplication> findAllByOrderByAppliedAtDesc();
}