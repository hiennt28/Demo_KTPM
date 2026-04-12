package com.vdqg.repository;

import com.vdqg.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByRoundId(Long roundId);

    List<Match> findByRoundSeasonIdOrderByMatchDateAsc(Long seasonId);
}
