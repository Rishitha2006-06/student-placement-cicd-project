package com.example.placement;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="job_applications") public class JobApplication {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private String studentName; private String email; private String status="Applied"; private LocalDateTime appliedAt=LocalDateTime.now();
 @ManyToOne(optional=false) private Job job;
 public JobApplication() {}
 public JobApplication(String n,String e,Job j){studentName=n;email=e;job=j;status="Applied";appliedAt=LocalDateTime.now();}
 public Long getId(){return id;} public String getStudentName(){return studentName;} public void setStudentName(String v){studentName=v;}
 public String getEmail(){return email;} public void setEmail(String v){email=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;}
 public Job getJob(){return job;} public void setJob(Job v){job=v;} public LocalDateTime getAppliedAt(){return appliedAt;}
}