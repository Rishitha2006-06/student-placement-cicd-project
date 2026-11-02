package com.example.placement;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final JobRepository jobRepository;

    public DataInitializer(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public void run(String... args) {
        if (jobRepository.count() == 0) {
            jobRepository.save(new Job(
                    "Software Development Engineer",
                    "Google",
                    "Bangalore, India",
                    "B.Tech CSE/IT, CGPA >= 8.0, No active backlogs"
            ));
            jobRepository.save(new Job(
                    "Cloud DevOps Engineer",
                    "Microsoft",
                    "Hyderabad, India",
                    "B.Tech/MCA, CGPA >= 7.5, Hands-on Docker & Linux"
            ));
            jobRepository.save(new Job(
                    "Data Analyst",
                    "Amazon",
                    "Chennai, India",
                    "B.Tech/BCA, CGPA >= 7.0, SQL & Python proficiency"
            ));
        }
    }
}
