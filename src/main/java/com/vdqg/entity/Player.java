// entity/Player.java
package com.vdqg.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "players")
@Data @NoArgsConstructor @AllArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    // Việt Nam | Nước ngoài
    @Column(name = "nationality")
    private String nationality;

    @Column(name = "position")
    private String position;             // Thủ môn | Hậu vệ | Tiền vệ | Tiền đạo
}