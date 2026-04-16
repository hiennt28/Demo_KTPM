package com.vdqg.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "awards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Award {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên giải thưởng không được để trống")
    @Column(name = "award_name", nullable = false)
    private String awardName;

    @NotBlank(message = "Loại giải không được để trống")
    @Column(name = "award_type")
    private String awardType;

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

    @Column(name = "status")
    private String status = "ĐANG ÁP DỤNG";
}
