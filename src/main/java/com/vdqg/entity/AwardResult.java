package com.vdqg.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "award_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AwardResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "award_id", nullable = false)
    private Award award;

    @Column(name = "bank_account")
    private String bankAccount;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "representative")
    private String representative;

    @Column(name = "payment_status")
    private String paymentStatus = "CHƯA THANH TOÁN";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @PrePersist
    @PreUpdate
    private void validateRecipient() {
        boolean hasTeam = team != null;
        boolean hasPlayer = player != null;
        if (hasTeam == hasPlayer) {
            throw new IllegalStateException("AwardResult phải gắn đúng một đối tượng nhận giải: Team hoặc Player.");
        }
    }
}
