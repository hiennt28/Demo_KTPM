package com.vdqg.repository;

import com.vdqg.entity.AwardPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AwardPaymentRepository extends JpaRepository<AwardPayment, Long> {
    boolean existsByAwardResultId(Long awardResultId);
}
