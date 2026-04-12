package com.vdqg.repository;

import com.vdqg.entity.MatchRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRegistrationRepository extends JpaRepository<MatchRegistration, Long> {
    boolean existsByMatchDetailId(Long matchDetailId);
}
