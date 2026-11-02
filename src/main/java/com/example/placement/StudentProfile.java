package com.example.placement;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_profile")
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name = "Rishitha";
    private String email = "rishitha@example.com";
    private String phone = "+91 98765 43210";
    private String rollNumber = "22CSE104";
    private String college = "CampusConnect Institute of Technology";
    private String degree = "B.Tech Computer Science & Engineering";
    private String batch = "2022 - 2026";
    private String cgpa = "8.5";
    
    @Column(length = 1000)
    private String skills = "Java, Spring Boot, MySQL, Docker, Kubernetes, Git, REST APIs, System Design";
    
    @Column(length = 1000)
    private String bio = "Passionate Computer Science student with a strong foundation in backend development, microservices, and cloud DevOps pipelines.";

    private String resumeFileName = "Rishitha_Resume_CSE.pdf";
    private int profileStrength = 85;

    private LocalDateTime updatedAt = LocalDateTime.now();

    public StudentProfile() {}

    public StudentProfile(String name, String email, String phone, String rollNumber, String college, String degree, String batch, String cgpa, String skills, String bio) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.rollNumber = rollNumber;
        this.college = college;
        this.degree = degree;
        this.batch = batch;
        this.cgpa = cgpa;
        this.skills = skills;
        this.bio = bio;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }

    public String getCgpa() { return cgpa; }
    public void setCgpa(String cgpa) { this.cgpa = cgpa; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getResumeFileName() { return resumeFileName; }
    public void setResumeFileName(String resumeFileName) { this.resumeFileName = resumeFileName; }

    public int getProfileStrength() { return profileStrength; }
    public void setProfileStrength(int profileStrength) { this.profileStrength = profileStrength; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
