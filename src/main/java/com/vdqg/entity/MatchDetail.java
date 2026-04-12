// entity/MatchDetail.java  ← Thực thể trung gian Match ↔ Team
package com.vdqg.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "match_details",
        uniqueConstraints = @UniqueConstraint(columnNames = {"match_id", "team_id"}))
@Data @NoArgsConstructor @AllArgsConstructor
public class MatchDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    // NHÀ | KHÁCH
    @Column(name = "role")
    private String role;

    @Column(name = "score")
    private Integer score = 0;

    // Kết quả sau trận: THẮNG | THUA | HÒA
    @Column(name = "result")
    private String result;
}