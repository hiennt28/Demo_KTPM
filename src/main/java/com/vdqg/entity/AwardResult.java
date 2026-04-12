// entity/AwardResult.java
package com.vdqg.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "award_results")
@Data @NoArgsConstructor @AllArgsConstructor
public class AwardResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "award_id", nullable = false)
    private Award award;

    // Nếu TẬP THỂ → liên kết Team
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    // Nếu CÁ NHÂN → liên kết PlayerContract
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_contract_id")
    private PlayerContract playerContract;

    // Thông tin thanh toán
    @Column(name = "bank_account")
    private String bankAccount;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "representative")
    private String representative;      // Người đại diện nhận thưởng

    // CHƯA THANH TOÁN | ĐÃ THANH TOÁN
    @Column(name = "payment_status")
    private String paymentStatus = "CHƯA THANH TOÁN";
}