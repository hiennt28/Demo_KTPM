// entity/Team.java
package com.vdqg.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teams")
@Data @NoArgsConstructor @AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @Column(name = "stadium")
    private String stadium;

    @Column(name = "coach")
    private String coach;

    @Column(name = "logo_url")
    private String logoUrl;
}