package com.example.placement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobApplicationRepository applicationRepository;

    private Job sampleJob;

    @BeforeEach
    void setUp() {
        applicationRepository.deleteAll();
        jobRepository.deleteAll();

        sampleJob = jobRepository.save(new Job("DevOps Engineer", "Microsoft", "Hyderabad", "B.Tech/MCA"));
    }

    @Test
    @DisplayName("GET / should display home page with job listings")
    void testHomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("jobs"))
                .andExpect(content().string(containsString("CampusConnect")))
                .andExpect(content().string(containsString("DevOps Engineer")))
                .andExpect(content().string(containsString("Microsoft")));
    }

    @Test
    @DisplayName("GET /jobs/new should show job creation form")
    void testNewJobForm() throws Exception {
        mockMvc.perform(get("/jobs/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("job-form"))
                .andExpect(model().attributeExists("job"))
                .andExpect(content().string(containsString("Add a job opening")));
    }

    @Test
    @DisplayName("POST /jobs should create a job and redirect to /")
    void testCreateJob() throws Exception {
        mockMvc.perform(post("/jobs")
                        .param("title", "Frontend Developer")
                        .param("company", "Meta")
                        .param("location", "Bangalore")
                        .param("eligibility", "Proficient in React & JavaScript"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message", "Job added successfully."));

        assertEquals(2, jobRepository.count());
    }

    @Test
    @DisplayName("POST /jobs/{id}/apply with valid data should succeed")
    void testApplySuccess() throws Exception {
        mockMvc.perform(post("/jobs/" + sampleJob.getId() + "/apply")
                        .param("studentName", "John Doe")
                        .param("email", "john.doe@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message", "Application submitted."));

        assertEquals(1, applicationRepository.count());
    }

    @Test
    @DisplayName("POST /jobs/{id}/apply with invalid email should be rejected")
    void testApplyInvalidEmail() throws Exception {
        mockMvc.perform(post("/jobs/" + sampleJob.getId() + "/apply")
                        .param("studentName", "John Doe")
                        .param("email", "invalid-email-address"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "Enter a valid name and email."));

        assertEquals(0, applicationRepository.count());
    }

    @Test
    @DisplayName("GET /applications should list all submitted applications")
    void testListApplications() throws Exception {
        applicationRepository.save(new JobApplication("Jane Doe", "jane@example.com", sampleJob));

        mockMvc.perform(get("/applications"))
                .andExpect(status().isOk())
                .andExpect(view().name("applications"))
                .andExpect(model().attributeExists("applications"))
                .andExpect(content().string(containsString("Jane Doe")))
                .andExpect(content().string(containsString("Applied")));
    }

    @Test
    @DisplayName("POST /applications/{id}/status should update applicant status")
    void testUpdateApplicationStatus() throws Exception {
        JobApplication app = applicationRepository.save(new JobApplication("Jane Doe", "jane@example.com", sampleJob));

        mockMvc.perform(post("/applications/" + app.getId() + "/status")
                        .param("status", "Shortlisted"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/applications"))
                .andExpect(flash().attribute("message", "Status updated."));

        JobApplication updated = applicationRepository.findById(app.getId()).orElseThrow();
        assertEquals("Shortlisted", updated.getStatus());
    }

    @Test
    @DisplayName("POST /applications/{id}/status with invalid status should reject")
    void testUpdateInvalidStatus() throws Exception {
        JobApplication app = applicationRepository.save(new JobApplication("Jane Doe", "jane@example.com", sampleJob));

        mockMvc.perform(post("/applications/" + app.getId() + "/status")
                        .param("status", "UnknownStatus"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/applications"))
                .andExpect(flash().attribute("error", "Invalid status."));
    }
}
