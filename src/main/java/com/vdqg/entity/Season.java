// entity/Season.java
package com.vdqg.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seasons")
@Data @NoArgsConstructor @AllArgsConstructor
public class Season {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "season_name", nullable = false, unique = true)
    private String seasonName;           // VD: "2024-2025"

    @Column(name = "status")
    private String status = "ĐANG DIỄN RA"; // ĐANG DIỄN RA | KẾT THÚC
}