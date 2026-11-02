package com.example.placement;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentName;
    private String email;
    private String status = "Applied";
    private LocalDateTime appliedAt = LocalDateTime.now();

    private String packageOffered = "";
    private String offerDate = "";
    private String interviewDate = "";
    private String appliedDateStr = "10 Nov 2026";

    @ManyToOne(optional = false)
    private Job job;

    public JobApplication() {}

    public JobApplication(String n, String e, Job j) {
        this.studentName = n;
        this.email = e;
        this.job = j;
        this.status = "Applied";
        this.appliedAt = LocalDateTime.now();
        this.appliedDateStr = "10 Nov 2026";
    }

    public JobApplication(String n, String e, Job j, String status, String pkg, String offerDate, String interviewDate, String appliedDateStr) {
        this.studentName = n;
        this.email = e;
        this.job = j;
        this.status = status;
        this.packageOffered = pkg;
        this.offerDate = offerDate;
        this.interviewDate = interviewDate;
        this.appliedDateStr = appliedDateStr;
        this.appliedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String v) { this.studentName = v; }

    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }

    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }

    public Job getJob() { return job; }
    public void setJob(Job v) { this.job = v; }

    public LocalDateTime getAppliedAt() { return appliedAt; }

    public String getPackageOffered() { return packageOffered; }
    public void setPackageOffered(String packageOffered) { this.packageOffered = packageOffered; }

    public String getOfferDate() { return offerDate; }
    public void setOfferDate(String offerDate) { this.offerDate = offerDate; }

    public String getInterviewDate() { return interviewDate; }
    public void setInterviewDate(String interviewDate) { this.interviewDate = interviewDate; }

    public String getAppliedDateStr() { return appliedDateStr; }
    public void setAppliedDateStr(String appliedDateStr) { this.appliedDateStr = appliedDateStr; }
}
