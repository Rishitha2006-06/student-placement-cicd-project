package com.example.placement;

import jakarta.persistence.*;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String subtitle;
    private String timeAgo;
    private String category;

    public Activity() {}

    public Activity(String title, String subtitle, String timeAgo, String category) {
        this.title = title;
        this.subtitle = subtitle;
        this.timeAgo = timeAgo;
        this.category = category;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getTimeAgo() { return timeAgo; }
    public void setTimeAgo(String timeAgo) { this.timeAgo = timeAgo; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
