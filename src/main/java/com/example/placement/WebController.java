package com.example.placement;
import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller public class WebController {
 private final JobRepository jobs; private final JobApplicationRepository apps;
 public WebController(JobRepository jobs,JobApplicationRepository apps){this.jobs=jobs;this.apps=apps;}
 @GetMapping("/") public String home(Model m){m.addAttribute("jobs",jobs.findAll());return "index";}
 @GetMapping("/jobs/new") public String form(Model m){m.addAttribute("job",new Job());return "job-form";}
 @PostMapping("/jobs") public String add(@ModelAttribute Job job,RedirectAttributes r){jobs.save(job);r.addFlashAttribute("message","Job added successfully.");return "redirect:/";}
 @PostMapping("/jobs/{id}/apply") public String apply(@PathVariable Long id,@RequestParam String studentName,@RequestParam String email,RedirectAttributes r){
 Job j=jobs.findById(id).orElse(null); if(j==null||studentName.isBlank()||!email.contains("@")){r.addFlashAttribute("error","Enter a valid name and email.");return "redirect:/";}
 apps.save(new JobApplication(studentName.trim(),email.trim(),j));r.addFlashAttribute("message","Application submitted.");return "redirect:/";}
 @GetMapping("/applications") public String list(Model m){m.addAttribute("applications",apps.findAllByOrderByAppliedAtDesc());return "applications";}
 @PostMapping("/applications/{id}/status") public String status(@PathVariable Long id,@RequestParam String status,RedirectAttributes r){
 if(!java.util.List.of("Applied","Shortlisted","Selected","Rejected").contains(status)){r.addFlashAttribute("error","Invalid status.");return "redirect:/applications";}
 apps.findById(id).ifPresent(a->{a.setStatus(status);apps.save(a);});r.addFlashAttribute("message","Status updated.");return "redirect:/applications";}
}