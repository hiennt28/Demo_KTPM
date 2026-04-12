package com.vdqg.repository;

import com.vdqg.entity.PlayerContract;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerContractRepository extends JpaRepository<PlayerContract, Long> {
    @EntityGraph(attributePaths = {"player", "team", "season"})
    List<PlayerContract> findByTeamIdAndSeasonIdAndStatus(Long teamId, Long seasonId, String status);

    @EntityGraph(attributePaths = {"player", "team", "season"})
    List<PlayerContract> findBySeasonIdAndStatus(Long seasonId, String status);

    @EntityGraph(attributePaths = {"player", "team", "season"})
    List<PlayerContract> findByTeamIdInAndSeasonIdAndStatus(List<Long> teamIds, Long seasonId, String status);
}
