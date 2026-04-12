package com.vdqg.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "awards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Award {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên giải thưởng không được để trống")
    @Column(name = "award_name", nullable = false)
    private String awardName;

    // CÁ NHÂN | TẬP THỂ
    @NotBlank(message = "Loại giải không được để trống")
    @Column(name = "award_type")
    private String awardType;

    // MÙA GIẢI | TRẬN ĐẤU
    @NotBlank(message = "Phạm vi áp dụng không được để trống")
    @Column(name = "scope")
    private String scope;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id")
    private Season season;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

    @NotNull(message = "Giá trị tiền thưởng không được để trống")
    @DecimalMin(value = "0.01", message = "Mức tiền thưởng phải lớn hơn 0")
    @Column(name = "prize_amount", nullable = false)
    private BigDecimal prizeAmount;

    @Column(name = "conditions", length = 500)
    private String conditions;

    // ĐANG ÁP DỤNG | HỦY ÁP DỤNG
    @Column(name = "status")
    private String status = "ĐANG ÁP DỤNG";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
