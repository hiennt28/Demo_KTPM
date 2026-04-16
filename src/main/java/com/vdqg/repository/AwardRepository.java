package com.vdqg.repository;

import com.vdqg.entity.Award;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AwardRepository extends JpaRepository<Award, Long> {
    List<Award> findByStatus(String status);

    List<Award> findBySeasonIdAndStatus(Long seasonId, String status);

    List<Award> findBySeasonSeasonNameAndStatus(String seasonName, String status);

    List<Award> findByMatchIdAndStatus(Long matchId, String status);

    boolean existsByAwardNameAndStatus(String awardName, String status);

    boolean existsByAwardNameAndStatusAndIdNot(String awardName, String status, Long id);
}
