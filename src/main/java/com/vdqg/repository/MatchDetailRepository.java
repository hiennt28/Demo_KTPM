package com.vdqg.repository;

import com.vdqg.entity.MatchDetail;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchDetailRepository extends JpaRepository<MatchDetail, Long> {
    @EntityGraph(attributePaths = "team")
    List<MatchDetail> findByMatchId(Long matchId);

    @EntityGraph(attributePaths = "team")
    List<MatchDetail> findByMatchIdIn(List<Long> matchIds);

    @EntityGraph(attributePaths = {"team", "match", "match.round", "match.round.season"})
    Optional<MatchDetail> findDetailedById(Long id);
}
