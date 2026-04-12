// entity/AwardPayment.java
package com.vdqg.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "award_payments",
        uniqueConstraints = @UniqueConstraint(columnNames = "award_result_id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AwardPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mỗi kết quả thưởng chỉ có tối đa một lần thanh toán
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "award_result_id", nullable = false)
    private AwardResult awardResult;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "note")
    private String note;
}
