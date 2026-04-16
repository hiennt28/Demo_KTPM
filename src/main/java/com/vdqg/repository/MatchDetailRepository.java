package com.vdqg.repository;

import com.vdqg.entity.MatchDetail;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchDetailRepository extends JpaRepository<MatchDetail, Long> {
    @Query("select d from Match m join m.matchDetails d left join fetch d.team where m.id = :matchId")
    List<MatchDetail> findByMatchId(@Param("matchId") Long matchId);

    @Query("select distinct d from Match m join m.matchDetails d left join fetch d.team where m.id in :matchIds")
    List<MatchDetail> findByMatchIdIn(@Param("matchIds") List<Long> matchIds);

    @EntityGraph(attributePaths = "team")
    Optional<MatchDetail> findWithTeamById(Long id);
}
