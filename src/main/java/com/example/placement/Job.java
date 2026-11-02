package com.example.placement;

import jakarta.persistence.*;

@Entity
@Table(name = "job")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String company;
    private String location;
    private String eligibility;
    private String jobType = "Full Time";
    private String packageLpa = "12 LPA";
    private String logo = "google";

    public Job() {}

    public Job(String title, String company, String location, String eligibility) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.eligibility = eligibility;
        this.jobType = "Full Time";
        this.packageLpa = "12 LPA";
        this.logo = resolveLogo(company);
    }

    public Job(String title, String company, String location, String eligibility, String jobType, String packageLpa, String logo) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.eligibility = eligibility;
        this.jobType = (jobType != null && !jobType.isBlank()) ? jobType : "Full Time";
        this.packageLpa = (packageLpa != null && !packageLpa.isBlank()) ? packageLpa : "12 LPA";
        this.logo = (logo != null && !logo.isBlank()) ? logo : resolveLogo(company);
    }

    private String resolveLogo(String companyName) {
        if (companyName == null) return "generic";
        String lower = companyName.toLowerCase();
        if (lower.contains("google")) return "google";
        if (lower.contains("microsoft")) return "microsoft";
        if (lower.contains("amazon")) return "amazon";
        if (lower.contains("accenture")) return "accenture";
        if (lower.contains("tech mahindra") || lower.contains("techmahindra")) return "techmahindra";
        return "generic";
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String v) { this.title = v; }

    public String getCompany() { return company; }
    public void setCompany(String v) {
        this.company = v;
        if (this.logo == null || this.logo.equals("generic")) {
            this.logo = resolveLogo(v);
        }
    }

    public String getLocation() { return location; }
    public void setLocation(String v) { this.location = v; }

    public String getEligibility() { return eligibility; }
    public void setEligibility(String v) { this.eligibility = v; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public String getPackageLpa() { return packageLpa; }
    public void setPackageLpa(String packageLpa) { this.packageLpa = packageLpa; }

    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }
}
