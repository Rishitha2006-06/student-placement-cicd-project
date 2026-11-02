package com.example.placement;
import jakarta.persistence.*;
@Entity public class Job {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private String title; private String company; private String location; private String eligibility;
 public Job() {}
 public Job(String title,String company,String location,String eligibility){this.title=title;this.company=company;this.location=location;this.eligibility=eligibility;}
 public Long getId(){return id;} public String getTitle(){return title;} public void setTitle(String v){title=v;}
 public String getCompany(){return company;} public void setCompany(String v){company=v;}
 public String getLocation(){return location;} public void setLocation(String v){location=v;}
 public String getEligibility(){return eligibility;} public void setEligibility(String v){eligibility=v;}
}