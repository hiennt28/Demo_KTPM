// entity/MatchRegistration.java
package com.vdqg.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "match_registrations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"match_detail_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_detail_id", nullable = false, unique = true)
    private MatchDetail matchDetail;

    // Lưu danh sách ID cầu thủ đá chính (comma-separated)
    @Column(name = "starting_player_ids", length = 500)
    private String startingPlayerIds;

    // Lưu danh sách ID cầu thủ dự bị
    @Column(name = "substitute_player_ids", length = 500)
    private String substitutePlayerIds;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @PrePersist
    public void prePersist() {
        this.registeredAt = LocalDateTime.now();
    }
}
