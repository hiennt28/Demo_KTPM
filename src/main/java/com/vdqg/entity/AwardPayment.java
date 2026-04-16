package com.vdqg.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "award_payments", uniqueConstraints = @UniqueConstraint(columnNames = "award_result_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AwardPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "note")
    private String note;

    @Column(name = "transaction_status")
    private String transactionStatus = "THÀNH CÔNG";

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "award_result_id", nullable = false)
    private AwardResult awardResult;
}
