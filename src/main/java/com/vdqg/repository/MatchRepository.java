package com.vdqg.repository;

import com.vdqg.entity.Match;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    @Override
    @EntityGraph(attributePaths = {"matchDetails", "matchDetails.team"})
    List<Match> findAll();

    @Override
    @EntityGraph(attributePaths = {"matchDetails", "matchDetails.team"})
    Optional<Match> findById(Long id);

    @EntityGraph(attributePaths = {"matchDetails", "matchDetails.team"})
    List<Match> findByIdIn(List<Long> ids);

    @EntityGraph(attributePaths = {"matchDetails", "matchDetails.team"})
    @Query("select distinct m from Round r join r.matches m where r.id = :roundId order by m.matchDate asc, m.id asc")
    List<Match> findByRoundId(@Param("roundId") Long roundId);

    @EntityGraph(attributePaths = {"matchDetails", "matchDetails.team"})
    @Query("select distinct m from Season s join s.rounds r join r.matches m where s.id = :seasonId order by m.matchDate asc, m.id asc")
    List<Match> findBySeasonIdOrderByMatchDateAsc(@Param("seasonId") Long seasonId);

    @EntityGraph(attributePaths = {"matchDetails", "matchDetails.team"})
    @Query("select distinct m from Match m join m.matchDetails md where md.id = :matchDetailId")
    Optional<Match> findByMatchDetailsId(@Param("matchDetailId") Long matchDetailId);
}
