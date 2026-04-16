package com.vdqg.repository;

import com.vdqg.entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeasonRepository extends JpaRepository<Season, Long> {
    List<Season> findByStatus(String status);

    @Query("select s from Season s join s.rounds r join r.matches m where m.id = :matchId")
    Optional<Season> findByMatchId(@Param("matchId") Long matchId);
}
