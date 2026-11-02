package com.example.placement;

import jakarta.persistence.*;

@Entity
@Table(name = "company_profile")
public class CompanyProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name = "TechCorp Solutions";
    private String industry = "Information Technology & Software Services";
    private String headquarters = "Bengaluru, Karnataka, India";
    private String website = "https://techcorp-innovations.example.com";
    private String contactEmail = "careers@techcorp.example.com";
    private String hrContact = "Sneha Verma (Head of University Talent)";
    private String employees = "2,500 - 5,000";

    @Column(length = 1000)
    private String description = "TechCorp is a leading enterprise cloud, AI, and developer tools engineering provider working with top global colleges to hire aspiring software developers and data engineers.";

    public CompanyProfile() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getHeadquarters() { return headquarters; }
    public void setHeadquarters(String headquarters) { this.headquarters = headquarters; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getHrContact() { return hrContact; }
    public void setHrContact(String hrContact) { this.hrContact = hrContact; }

    public String getEmployees() { return employees; }
    public void setEmployees(String employees) { this.employees = employees; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
