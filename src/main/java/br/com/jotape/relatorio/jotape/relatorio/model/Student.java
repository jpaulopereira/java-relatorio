package br.com.jotape.relatorio.jotape.relatorio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "student")
public class Student {

    @Id
    private String id;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private LocalDate birthday;

    @ManyToOne
    private School school;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Student(String name, String email, String password, LocalDate birthday, School school) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.school = school;
    }

    @PrePersist
    private void prePersist() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }

}
