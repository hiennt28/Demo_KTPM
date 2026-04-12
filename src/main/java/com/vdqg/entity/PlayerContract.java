// entity/PlayerContract.java  ← Thực thể trung gian Player ↔ Team
package com.vdqg.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "player_contracts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"player_id","team_id","season_id"}))
@Data @NoArgsConstructor @AllArgsConstructor
public class PlayerContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @Column(name = "jersey_number")
    private Integer jerseyNumber;

    // BÌNH THƯỜNG | CHẤN THƯƠNG | ĐÌNH CHỈ
    @Column(name = "health_status")
    private String healthStatus = "BÌNH THƯỜNG";

    @Column(name = "yellow_cards")
    private Integer yellowCards = 0;

    @Column(name = "red_cards")
    private Integer redCards = 0;

    @Column(name = "contract_start")
    private LocalDate contractStart;

    @Column(name = "contract_end")
    private LocalDate contractEnd;

    // HOẠT ĐỘNG | HẾT HẠN
    @Column(name = "status")
    private String status = "HOẠT ĐỘNG";
}