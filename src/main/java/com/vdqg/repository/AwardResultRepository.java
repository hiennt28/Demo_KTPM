package com.vdqg.repository;

import com.vdqg.entity.AwardResult;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AwardResultRepository extends JpaRepository<AwardResult, Long> {
    @EntityGraph(attributePaths = {"award", "team", "player"})
    List<AwardResult> findByAwardId(Long awardId);

    @EntityGraph(attributePaths = {"award", "team", "player"})
    Optional<AwardResult> findDetailedById(Long id);

    boolean existsByAwardIdAndTeamId(Long awardId, Long teamId);

    boolean existsByAwardIdAndTeamIdAndIdNot(Long awardId, Long teamId, Long id);

    boolean existsByAwardIdAndPlayerId(Long awardId, Long playerId);

    boolean existsByAwardIdAndPlayerIdAndIdNot(Long awardId, Long playerId, Long id);
}
