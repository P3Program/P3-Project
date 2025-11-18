package com.p3program.laugin_project_planner.projects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Links this note to a specific project
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @JsonBackReference // Prevents infinite loop when converting to JSON (Note->Project->Notes->Project...)
    private Project project;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String username;

    // Empty constructor (needed by JPA)

    public Note() {}
    // Constructor to create a new note

    public Note(Project project, String text, String username) {
        this.project = project;
        this.text = text;
        this.timestamp = LocalDateTime.now();
        this.username = username;
    }
    // Getters and setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser() {
        return username;
    }

    public void setUser(String username) {
        this.username = username;
    }
}