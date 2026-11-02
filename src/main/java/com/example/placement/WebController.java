package com.example.placement;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WebController {

    private final JobRepository jobs;
    private final JobApplicationRepository apps;
    private final ActivityRepository activities;
    private final StudentProfileRepository profileRepository;
    private final CompanyProfileRepository companyProfileRepository;

    public WebController(JobRepository jobs,
                         JobApplicationRepository apps,
                         ActivityRepository activities,
                         StudentProfileRepository profileRepository,
                         CompanyProfileRepository companyProfileRepository) {
        this.jobs = jobs;
        this.apps = apps;
        this.activities = activities;
        this.profileRepository = profileRepository;
        this.companyProfileRepository = companyProfileRepository;
    }

    private StudentProfile getOrCreateProfile() {
        return profileRepository.findFirstByOrderByIdAsc().orElseGet(() -> {
            StudentProfile p = new StudentProfile();
            return profileRepository.save(p);
        });
    }

    private CompanyProfile getOrCreateCompanyProfile() {
        return companyProfileRepository.findFirstByOrderByIdAsc().orElseGet(() -> {
            CompanyProfile cp = new CompanyProfile();
            return companyProfileRepository.save(cp);
        });
    }

    // ==========================================
    // 1. STUDENT PORTAL
    // ==========================================

    // Student Dashboard (Home / or /student)
    @GetMapping({"/", "/student"})
    public String home(Model m) {
        List<Job> allJobs = jobs.findAll();
        StudentProfile profile = getOrCreateProfile();

        m.addAttribute("jobs", allJobs);
        m.addAttribute("activeNav", "dashboard");
        m.addAttribute("role", "student");
        m.addAttribute("profile", profile);

        // Metrics for Student Dashboard
        m.addAttribute("totalJobsCount", allJobs.size());
        m.addAttribute("myApplicationsCount", apps.findAll().stream()
                .filter(a -> a.getStudentName() != null && a.getStudentName().equalsIgnoreCase(profile.getName()))
                .count());
        m.addAttribute("selectedCount", apps.findAll().stream()
                .filter(a -> a.getStudentName() != null && a.getStudentName().equalsIgnoreCase(profile.getName()) && "Selected".equalsIgnoreCase(a.getStatus()))
                .count());
        m.addAttribute("profileStrength", profile.getProfileStrength());
        m.addAttribute("placementProgress", 80);

        return "index";
    }

    // Student Profile & Resume Page
    @GetMapping("/student/profile")
    public String studentProfilePage(Model m) {
        StudentProfile profile = getOrCreateProfile();
        m.addAttribute("profile", profile);
        m.addAttribute("activeNav", "profile");
        m.addAttribute("role", "student");
        return "student-profile";
    }

    // Update Profile Information
    @PostMapping("/student/profile/edit")
    public String updateProfile(@ModelAttribute StudentProfile formProfile, RedirectAttributes r) {
        StudentProfile p = getOrCreateProfile();
        if (formProfile.getName() != null && !formProfile.getName().isBlank()) p.setName(formProfile.getName().trim());
        if (formProfile.getEmail() != null && !formProfile.getEmail().isBlank()) p.setEmail(formProfile.getEmail().trim());
        if (formProfile.getPhone() != null) p.setPhone(formProfile.getPhone().trim());
        if (formProfile.getCollege() != null) p.setCollege(formProfile.getCollege().trim());
        if (formProfile.getDegree() != null) p.setDegree(formProfile.getDegree().trim());
        if (formProfile.getBatch() != null) p.setBatch(formProfile.getBatch().trim());
        if (formProfile.getCgpa() != null) p.setCgpa(formProfile.getCgpa().trim());
        if (formProfile.getSkills() != null) p.setSkills(formProfile.getSkills().trim());
        if (formProfile.getBio() != null) p.setBio(formProfile.getBio().trim());
        p.setUpdatedAt(LocalDateTime.now());
        profileRepository.save(p);

        r.addFlashAttribute("message", "Profile details updated successfully.");
        return "redirect:/student/profile";
    }

    // Upload Resume
    @PostMapping("/student/profile/resume")
    public String uploadResume(@RequestParam(value = "resumeFile", required = false) MultipartFile resumeFile,
                               @RequestParam(value = "simulatedFileName", required = false) String simulatedFileName,
                               RedirectAttributes r) {
        StudentProfile p = getOrCreateProfile();
        String finalName = "Rishitha_Resume_2026.pdf";

        if (resumeFile != null && !resumeFile.isEmpty()) {
            finalName = resumeFile.getOriginalFilename();
        } else if (simulatedFileName != null && !simulatedFileName.isBlank()) {
            finalName = simulatedFileName.trim();
        }

        p.setResumeFileName(finalName);
        p.setProfileStrength(100); // 100% when resume is uploaded
        p.setUpdatedAt(LocalDateTime.now());
        profileRepository.save(p);

        activities.save(new Activity("Resume updated", p.getName() + " • Just now", "Just now", "application"));

        r.addFlashAttribute("message", "Resume uploaded successfully: " + finalName + " (Profile Strength updated to 100%)");
        return "redirect:/student/profile";
    }

    // Student Applications List
    @GetMapping("/student/applications")
    public String studentApplications(Model m) {
        StudentProfile profile = getOrCreateProfile();
        List<JobApplication> myApps = apps.findAll().stream()
                .filter(a -> a.getStudentName() != null && a.getStudentName().equalsIgnoreCase(profile.getName()))
                .toList();

        // If no apps for this name yet, show all sample applications so user sees full UI
        if (myApps.isEmpty()) {
            myApps = apps.findAll();
        }

        m.addAttribute("applications", myApps);
        m.addAttribute("profile", profile);
        m.addAttribute("activeNav", "applications");
        m.addAttribute("role", "student");
        return "student-applications";
    }

    // Student Placement Status Page
    @GetMapping("/student/placement-status")
    public String placementStatus(Model m) {
        StudentProfile profile = getOrCreateProfile();
        m.addAttribute("activeNav", "placement-status");
        m.addAttribute("role", "student");
        m.addAttribute("profile", profile);

        JobApplication selectedApp = apps.findAll().stream()
                .filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase("Selected"))
                .findFirst()
                .orElse(null);

        m.addAttribute("selectedApp", selectedApp);
        return "placement-status";
    }

    // Apply for Job
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
        JobApplication application = new JobApplication(studentName.trim(), email.trim(), j);
        apps.save(application);
        activities.save(new Activity("New application received", studentName.trim() + " for " + j.getTitle() + " • Just now", "Just now", "application"));
        r.addFlashAttribute("message", "Application submitted.");
        return "redirect:/";
    }

    // ==========================================
    // 2. COMPANY PORTAL
    // ==========================================

    @GetMapping("/company")
    public String companyDashboard(Model m) {
        m.addAttribute("activeNav", "dashboard");
        m.addAttribute("role", "company");

        List<Job> allJobs = jobs.findAll();
        List<JobApplication> allApps = apps.findAllByOrderByAppliedAtDesc();
        CompanyProfile cp = getOrCreateCompanyProfile();

        m.addAttribute("companyProfile", cp);
        m.addAttribute("postedJobsCount", 3);
        m.addAttribute("totalApplicantsCount", 120);
        m.addAttribute("shortlistedCount", 25);
        m.addAttribute("hiredCount", 12);

        m.addAttribute("recentApplications", allApps);
        m.addAttribute("companyJobs", allJobs);
        return "company-dashboard";
    }

    @GetMapping("/company/jobs/new")
    public String companyJobForm(Model m) {
        Job job = new Job();
        job.setCompany("TechCorp");
        m.addAttribute("job", job);
        m.addAttribute("activeNav", "post-job");
        m.addAttribute("role", "company");
        return "company-job-form";
    }

    @PostMapping("/company/jobs")
    public String companySaveJob(@ModelAttribute Job job, RedirectAttributes r) {
        if (job.getCompany() == null || job.getCompany().isBlank()) {
            job.setCompany("TechCorp");
        }
        jobs.save(job);
        activities.save(new Activity("New job posted", job.getTitle() + " - " + job.getCompany() + " • Just now", "Just now", "job"));
        r.addFlashAttribute("message", "Job opening posted successfully for " + job.getCompany() + ".");
        return "redirect:/company/jobs";
    }

    @GetMapping("/company/jobs")
    public String companyJobsList(Model m) {
        m.addAttribute("jobs", jobs.findAll());
        m.addAttribute("activeNav", "jobs");
        m.addAttribute("role", "company");
        return "company-jobs";
    }

    @GetMapping("/company/applicants")
    public String companyApplicants(Model m) {
        m.addAttribute("applications", apps.findAllByOrderByAppliedAtDesc());
        m.addAttribute("activeNav", "applicants");
        m.addAttribute("role", "company");
        return "company-applicants";
    }

    @GetMapping("/company/shortlisted")
    public String companyShortlisted(Model m) {
        List<JobApplication> shortlisted = apps.findAll().stream()
                .filter(a -> a.getStatus() != null && ("Shortlisted".equalsIgnoreCase(a.getStatus()) || "Selected".equalsIgnoreCase(a.getStatus())))
                .toList();
        m.addAttribute("applications", shortlisted);
        m.addAttribute("activeNav", "shortlisted");
        m.addAttribute("role", "company");
        return "company-shortlisted";
    }

    @GetMapping("/company/profile")
    public String companyProfile(Model m) {
        m.addAttribute("company", getOrCreateCompanyProfile());
        m.addAttribute("activeNav", "profile");
        m.addAttribute("role", "company");
        return "company-profile";
    }

    @PostMapping("/company/profile")
    public String updateCompanyProfile(@ModelAttribute CompanyProfile form, RedirectAttributes r) {
        CompanyProfile cp = getOrCreateCompanyProfile();
        if (form.getName() != null && !form.getName().isBlank()) cp.setName(form.getName().trim());
        if (form.getIndustry() != null) cp.setIndustry(form.getIndustry().trim());
        if (form.getHeadquarters() != null) cp.setHeadquarters(form.getHeadquarters().trim());
        if (form.getWebsite() != null) cp.setWebsite(form.getWebsite().trim());
        if (form.getContactEmail() != null) cp.setContactEmail(form.getContactEmail().trim());
        if (form.getHrContact() != null) cp.setHrContact(form.getHrContact().trim());
        if (form.getDescription() != null) cp.setDescription(form.getDescription().trim());
        companyProfileRepository.save(cp);

        r.addFlashAttribute("message", "Company profile updated successfully.");
        return "redirect:/company/profile";
    }

    @PostMapping("/company/applicants/{id}/status")
    public String updateCompanyApplicantStatus(@PathVariable Long id,
                                               @RequestParam String status,
                                               RedirectAttributes r) {
        if (!List.of("Applied", "Shortlisted", "Selected", "Rejected").contains(status)) {
            r.addFlashAttribute("error", "Invalid status.");
            return "redirect:/company/applicants";
        }
        apps.findById(id).ifPresent(a -> {
            a.setStatus(status);
            apps.save(a);
            if ("Selected".equalsIgnoreCase(status)) {
                activities.save(new Activity("Student placed", a.getStudentName() + " placed at " + a.getJob().getCompany() + " • Just now", "Just now", "placed"));
            }
        });
        r.addFlashAttribute("message", "Applicant status updated to " + status + ".");
        return "redirect:/company/applicants";
    }

    // ==========================================
    // 3. ADMIN PORTAL
    // ==========================================

    @GetMapping("/admin")
    public String adminDashboard(Model m) {
        m.addAttribute("activeNav", "dashboard");
        m.addAttribute("role", "admin");

        List<Job> allJobs = jobs.findAll();
        m.addAttribute("totalStudents", 250);
        m.addAttribute("registeredCompanies", 45);
        m.addAttribute("totalJobs", allJobs.isEmpty() ? 120 : allJobs.size() + 115);
        m.addAttribute("placedStudents", 180);

        m.addAttribute("activities", activities.findTop10ByOrderByIdDesc());
        m.addAttribute("recentApps", apps.findAllByOrderByAppliedAtDesc());
        return "admin-dashboard";
    }

    @GetMapping("/admin/students")
    public String adminStudents(Model m) {
        StudentProfile p = getOrCreateProfile();
        m.addAttribute("profile", p);
        m.addAttribute("activeNav", "students");
        m.addAttribute("role", "admin");
        return "admin-students";
    }

    @PostMapping("/admin/students")
    public String adminAddStudent(@RequestParam String name,
                                  @RequestParam String email,
                                  @RequestParam String department,
                                  @RequestParam String cgpa,
                                  RedirectAttributes r) {
        activities.save(new Activity("New student registered", name.trim() + " (" + department + ", CGPA " + cgpa + ") • Just now", "Just now", "application"));
        r.addFlashAttribute("message", "Student " + name + " registered successfully.");
        return "redirect:/admin/students";
    }

    @GetMapping("/admin/companies")
    public String adminCompanies(Model m) {
        m.addAttribute("activeNav", "companies");
        m.addAttribute("role", "admin");
        m.addAttribute("company", getOrCreateCompanyProfile());
        return "admin-companies";
    }

    @PostMapping("/admin/companies")
    public String adminAddCompany(@RequestParam String name,
                                  @RequestParam String location,
                                  @RequestParam String sector,
                                  RedirectAttributes r) {
        activities.save(new Activity("New company registered", name.trim() + " • Just now", "Just now", "company"));
        r.addFlashAttribute("message", "Company " + name + " added to campus placement network.");
        return "redirect:/admin/companies";
    }

    @GetMapping("/admin/jobs")
    public String adminJobs(Model m) {
        m.addAttribute("jobs", jobs.findAll());
        m.addAttribute("activeNav", "jobs");
        m.addAttribute("role", "admin");
        return "admin-jobs";
    }

    @GetMapping("/admin/applications")
    public String adminApplications(Model m) {
        return list(m);
    }

    @GetMapping("/admin/reports")
    public String adminReports(Model m) {
        m.addAttribute("activeNav", "reports");
        m.addAttribute("role", "admin");
        m.addAttribute("totalStudents", 250);
        m.addAttribute("totalPlaced", 180);
        m.addAttribute("highestPackage", "44 LPA (Google)");
        m.addAttribute("avgPackage", "14.5 LPA");
        return "admin-reports";
    }

    @GetMapping("/admin/settings")
    public String adminSettings(Model m) {
        m.addAttribute("activeNav", "settings");
        m.addAttribute("role", "admin");
        return "admin-settings";
    }

    // ==========================================
    // 4. SHARED / LEGACY COMPATIBILITY ENDPOINTS
    // ==========================================

    @GetMapping("/jobs/new")
    public String form(Model m) {
        m.addAttribute("job", new Job());
        m.addAttribute("activeNav", "jobs");
        m.addAttribute("role", "admin");
        return "job-form";
    }

    @PostMapping("/jobs")
    public String add(@ModelAttribute Job job, RedirectAttributes r) {
        jobs.save(job);
        activities.save(new Activity("New job posted", job.getTitle() + " - " + job.getCompany() + " • Just now", "Just now", "job"));
        r.addFlashAttribute("message", "Job added successfully.");
        return "redirect:/";
    }

    @GetMapping("/applications")
    public String list(Model m) {
        m.addAttribute("applications", apps.findAllByOrderByAppliedAtDesc());
        m.addAttribute("activeNav", "applications");
        m.addAttribute("role", "admin");
        return "applications";
    }

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
            if ("Selected".equalsIgnoreCase(status)) {
                activities.save(new Activity("Student placed", a.getStudentName() + " placed at " + a.getJob().getCompany() + " • Just now", "Just now", "placed"));
            }
        });
        r.addFlashAttribute("message", "Status updated.");
        return "redirect:/applications";
    }
}
