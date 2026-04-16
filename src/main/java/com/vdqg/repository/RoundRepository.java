package com.vdqg.repository;

import com.vdqg.entity.Round;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoundRepository extends JpaRepository<Round, Long> {
    @EntityGraph(attributePaths = "matches")
    @Query("select r from Season s join s.rounds r where s.id = :seasonId order by r.roundOrder asc, r.id asc")
    List<Round> findBySeasonIdOrderByRoundOrder(@Param("seasonId") Long seasonId);

    @Query("select r from Round r join r.matches m where m.id = :matchId")
    Optional<Round> findByMatchesId(@Param("matchId") Long matchId);
}
