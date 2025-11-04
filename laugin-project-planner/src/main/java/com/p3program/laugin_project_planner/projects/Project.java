package com.p3program.laugin_project_planner.projects;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects_test")
public class Project {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 45)
    private String title;

    @Column(length = 45)
    private String name;

    @Column
    @CreationTimestamp
    private LocalDate date;

    @Column
    private boolean caldera;

    @Column
    private boolean warranty;

    @Column
    private String ssn;

    @Column
    private String phoneNum;

    @Column(length = 45)
    private String address;

    @Column
    private String email;

    @Column
    private int hours;

    @Column
    private Date estDueDate;

    @Column
    private String priority;

    @Column
    private String description;

    @Column
    private String status;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Prevents infinite loop when converting to JSON (Note->Project->Notes->Project...)
    // list array to store all the notes that belong to the selected project
    private List<Note> notes = new ArrayList<>();

    public Project() {
        if (this.status == null) {
            this.status = "allProjects";
        }
    }

    public Project(long id, String title, String name, LocalDate date, boolean caldera, boolean warranty, String ssn, String phoneNum, String address, String email, int hours, Date estDueDate, String priority, String description, String status) {
        this.id = id;
        this.title = title;
        this.name = name;
        this.date = date;
        this.caldera = caldera;
        this.warranty = warranty;
        this.ssn = ssn;
        this.phoneNum = phoneNum;
        this.address = address;
        this.email = email;
        this.hours = hours;
        this.estDueDate = estDueDate;
        this.priority = priority;
        this.description = description;
        this.status = status;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isCaldera() {
        return caldera;
    }

    public void setCaldera(boolean caldera) {
        this.caldera = caldera;
    }

    public boolean isWarranty() {
        return warranty;
    }

    public void setWarranty(boolean warranty) {
        this.warranty = warranty;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public Date getEstDueDate() {
        return estDueDate;
    }

    public void setEstDueDate(Date estDueDate) {
        this.estDueDate = estDueDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}