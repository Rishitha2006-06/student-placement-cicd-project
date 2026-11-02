package com.example.placement;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class WebController {

    private final JobRepository jobs;
    private final JobApplicationRepository apps;
    private final ActivityRepository activities;

    public WebController(JobRepository jobs, JobApplicationRepository apps, ActivityRepository activities) {
        this.jobs = jobs;
        this.apps = apps;
        this.activities = activities;
    }

    // 1. Student Dashboard (Home /)
    @GetMapping({"/", "/student"})
    public String home(Model m) {
        List<Job> allJobs = jobs.findAll();
        m.addAttribute("jobs", allJobs);
        m.addAttribute("activeNav", "dashboard");
        m.addAttribute("role", "student");

        // Metrics for Student Dashboard
        m.addAttribute("totalJobsCount", allJobs.size());
        m.addAttribute("myApplicationsCount", apps.findAll().stream().filter(a -> a.getStudentName().equalsIgnoreCase("Rishitha")).count());
        m.addAttribute("selectedCount", apps.findAll().stream().filter(a -> a.getStudentName().equalsIgnoreCase("Rishitha") && a.getStatus().equalsIgnoreCase("Selected")).count());
        m.addAttribute("profileStrength", 85);
        m.addAttribute("placementProgress", 80);

        return "index";
    }

    // 2. Student Placement Status Page
    @GetMapping("/student/placement-status")
    public String placementStatus(Model m) {
        m.addAttribute("activeNav", "placement-status");
        m.addAttribute("role", "student");

        // Find Rishitha's selected offer or provide default
        JobApplication selectedApp = apps.findAll().stream()
                .filter(a -> a.getStatus().equalsIgnoreCase("Selected"))
                .findFirst()
                .orElse(null);

        m.addAttribute("selectedApp", selectedApp);
        return "placement-status";
    }

    // 3. Admin Dashboard
    @GetMapping("/admin")
    public String adminDashboard(Model m) {
        m.addAttribute("activeNav", "dashboard");
        m.addAttribute("role", "admin");

        List<Job> allJobs = jobs.findAll();
        List<JobApplication> allApps = apps.findAll();

        m.addAttribute("totalStudents", 250);
        m.addAttribute("registeredCompanies", 45);
        m.addAttribute("totalJobs", allJobs.isEmpty() ? 120 : allJobs.size() + 115);
        m.addAttribute("placedStudents", 180);

        m.addAttribute("activities", activities.findTop10ByOrderByIdDesc());
        m.addAttribute("recentApps", apps.findAllByOrderByAppliedAtDesc());
        return "admin-dashboard";
    }

    // 4. Company Dashboard
    @GetMapping("/company")
    public String companyDashboard(Model m) {
        m.addAttribute("activeNav", "dashboard");
        m.addAttribute("role", "company");

        List<Job> allJobs = jobs.findAll();
        List<JobApplication> allApps = apps.findAllByOrderByAppliedAtDesc();

        m.addAttribute("postedJobsCount", 3);
        m.addAttribute("totalApplicantsCount", 120);
        m.addAttribute("shortlistedCount", 25);
        m.addAttribute("hiredCount", 12);

        m.addAttribute("recentApplications", allApps);
        m.addAttribute("companyJobs", allJobs);
        return "company-dashboard";
    }

    // 5. Job Creation Form
    @GetMapping("/jobs/new")
    public String form(Model m) {
        m.addAttribute("job", new Job());
        m.addAttribute("activeNav", "jobs");
        m.addAttribute("role", "admin");
        return "job-form";
    }

    // 6. Save Job
    @PostMapping("/jobs")
    public String add(@ModelAttribute Job job, RedirectAttributes r) {
        jobs.save(job);
        r.addFlashAttribute("message", "Job added successfully.");
        return "redirect:/";
    }

    // 7. Student Apply for Job
    @PostMapping("/jobs/{id}/apply")
    public String apply(@PathVariable Long id,
                        @RequestParam String studentName,
                        @RequestParam String email,
                        RedirectAttributes r) {
        Job j = jobs.findById(id).orElse(null);
        if (j == null || studentName.isBlank() || !email.contains("@")) {
            r.addFlashAttribute("error", "Enter a valid name and email.");
            return "redirect:/";
        }
        apps.save(new JobApplication(studentName.trim(), email.trim(), j));
        r.addFlashAttribute("message", "Application submitted.");
        return "redirect:/";
    }

    // 8. View Applications List
    @GetMapping("/applications")
    public String list(Model m) {
        m.addAttribute("applications", apps.findAllByOrderByAppliedAtDesc());
        m.addAttribute("activeNav", "applications");
        m.addAttribute("role", "admin");
        return "applications";
    }

    // 9. Update Application Status
    @PostMapping("/applications/{id}/status")
    public String status(@PathVariable Long id,
                         @RequestParam String status,
                         RedirectAttributes r) {
        if (!List.of("Applied", "Shortlisted", "Selected", "Rejected").contains(status)) {
            r.addFlashAttribute("error", "Invalid status.");
            return "redirect:/applications";
        }
        apps.findById(id).ifPresent(a -> {
            a.setStatus(status);
            apps.save(a);
        });
        r.addFlashAttribute("message", "Status updated.");
        return "redirect:/applications";
    }
}
