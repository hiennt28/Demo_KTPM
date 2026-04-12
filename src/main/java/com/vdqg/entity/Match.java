// entity/Match.java
package com.vdqg.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    @Column(name = "match_date")
    private LocalDateTime matchDate;

    @Column(name = "stadium")
    private String stadium;

    // CHƯA DIỄN RA | ĐANG DIỄN RA | KẾT THÚC
    @Column(name = "status")
    private String status = "CHƯA DIỄN RA";

    // Quan hệ n-n với Team qua MatchDetail
    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MatchDetail> matchDetails;
}
