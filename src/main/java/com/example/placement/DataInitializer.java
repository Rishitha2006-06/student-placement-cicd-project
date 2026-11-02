package com.example.placement;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;
    private final ActivityRepository activityRepository;

    public DataInitializer(JobRepository jobRepository,
                           JobApplicationRepository applicationRepository,
                           ActivityRepository activityRepository) {
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.activityRepository = activityRepository;
    }

    @Override
    public void run(String... args) {
        if (jobRepository.count() == 0) {
            Job google = jobRepository.save(new Job(
                    "Software Engineer",
                    "Google",
                    "Bengaluru",
                    "B.Tech CSE/IT, CGPA >= 8.0, Strong DSA & System Design",
                    "Full Time",
                    "18 LPA",
                    "google"
            ));

            Job microsoft = jobRepository.save(new Job(
                    "Data Scientist",
                    "Microsoft",
                    "Hyderabad",
                    "B.Tech/MCA, CGPA >= 7.5, ML, Python & SQL proficiency",
                    "Full Time",
                    "24 LPA",
                    "microsoft"
            ));

            Job amazon = jobRepository.save(new Job(
                    "SDE Intern",
                    "Amazon",
                    "Chennai",
                    "B.Tech/BCA, CGPA >= 7.0, Java/C++ and Web development",
                    "Internship",
                    "12 LPA",
                    "amazon"
            ));

            Job accenture = jobRepository.save(new Job(
                    "SDE",
                    "Accenture",
                    "Pune",
                    "Any Graduate with min 65% aggregate, Problem solving",
                    "Full Time",
                    "8 LPA",
                    "accenture"
            ));

            Job techm = jobRepository.save(new Job(
                    "Associate Consultant",
                    "Tech Mahindra",
                    "Noida",
                    "B.Tech / B.Sc (IT), Good communication & technical skills",
                    "Full Time",
                    "7 LPA",
                    "techmahindra"
            ));

            // Seed Applications matching the screens
            applicationRepository.save(new JobApplication(
                    "Rishitha",
                    "rishitha@example.com",
                    google,
                    "Selected",
                    "18 LPA",
                    "15 Dec 2026",
                    "20 Nov 2026",
                    "10 Nov 2026"
            ));

            applicationRepository.save(new JobApplication(
                    "Rishitha",
                    "rishitha@example.com",
                    microsoft,
                    "Applied",
                    "24 LPA",
                    "Pending",
                    "TBD",
                    "12 Nov 2026"
            ));

            applicationRepository.save(new JobApplication(
                    "Ananya Sharma",
                    "ananya.sharma@example.com",
                    google,
                    "Shortlisted",
                    "18 LPA",
                    "Pending",
                    "25 Nov 2026",
                    "15 Nov 2026"
            ));

            applicationRepository.save(new JobApplication(
                    "Rohan Mehta",
                    "rohan.mehta@example.com",
                    microsoft,
                    "Applied",
                    "24 LPA",
                    "Pending",
                    "TBD",
                    "18 Nov 2026"
            ));

            applicationRepository.save(new JobApplication(
                    "Divya Nair",
                    "divya.nair@example.com",
                    amazon,
                    "Applied",
                    "12 LPA",
                    "Pending",
                    "TBD",
                    "19 Nov 2026"
            ));
        }

        if (activityRepository.count() == 0) {
            activityRepository.save(new Activity("New company registered", "Tech Mahindra • 2 mins ago", "2 mins ago", "company"));
            activityRepository.save(new Activity("New job posted", "SDE - Accenture • 5 mins ago", "5 mins ago", "job"));
            activityRepository.save(new Activity("Student placed", "Sneha Reddy • 10 mins ago", "10 mins ago", "placed"));
            activityRepository.save(new Activity("New application received", "Rahul Kumar • 12 mins ago", "12 mins ago", "application"));
        }
    }
}
