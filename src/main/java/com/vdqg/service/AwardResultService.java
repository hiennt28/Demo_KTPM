package com.vdqg.service;

import com.vdqg.entity.Award;
import com.vdqg.entity.AwardResult;
import com.vdqg.entity.MatchDetail;
import com.vdqg.entity.PlayerContract;
import com.vdqg.entity.Team;
import com.vdqg.repository.AwardRepository;
import com.vdqg.repository.AwardResultRepository;
import com.vdqg.repository.MatchDetailRepository;
import com.vdqg.repository.PlayerContractRepository;
import com.vdqg.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AwardResultService {

    private static final String TEAM_AWARD = "T\u1eacP TH\u1ec2";
    private static final String INDIVIDUAL_AWARD = "C\u00c1 NH\u00c2N";
    private static final String ACTIVE_CONTRACT = "HO\u1ea0T \u0110\u1ed8NG";
    private static final String UNPAID_STATUS = "CH\u01afA THANH TO\u00c1N";

    private final AwardRepository awardRepository;
    private final AwardResultRepository awardResultRepository;
    private final TeamRepository teamRepository;
    private final PlayerContractRepository playerContractRepository;
    private final MatchDetailRepository matchDetailRepository;

    public Award findAwardById(Long awardId) {
        return awardRepository.findById(awardId)
                .orElseThrow(() -> new RuntimeException("Kh\u00f4ng t\u00ecm th\u1ea5y gi\u1ea3i th\u01b0\u1edfng!"));
    }

    public AwardResult findResultById(Long resultId) {
        return awardResultRepository.findDetailedById(resultId)
                .orElseThrow(() -> new RuntimeException("Kh\u00f4ng t\u00ecm th\u1ea5y ng\u01b0\u1eddi nh\u1eadn gi\u1ea3i!"));
    }

    public List<AwardResult> getResultsByAward(Long awardId) {
        return awardResultRepository.findByAwardId(awardId);
    }

    public List<Team> getTeamOptions(Award award) {
        if (award.getMatch() != null) {
            return matchDetailRepository.findByMatchId(award.getMatch().getId()).stream()
                    .map(MatchDetail::getTeam)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(Team::getTeamName, String.CASE_INSENSITIVE_ORDER))
                    .toList();
        }

        return teamRepository.findAll().stream()
                .sorted(Comparator.comparing(Team::getTeamName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public List<PlayerContract> getPlayerOptions(Award award) {
        if (award.getMatch() != null) {
            List<Long> teamIds = matchDetailRepository.findByMatchId(award.getMatch().getId()).stream()
                    .map(MatchDetail::getTeam)
                    .filter(Objects::nonNull)
                    .map(Team::getId)
                    .toList();
            Long seasonId = award.getMatch().getRound().getSeason().getId();

            return playerContractRepository.findByTeamIdInAndSeasonIdAndStatus(teamIds, seasonId, ACTIVE_CONTRACT)
                    .stream()
                    .sorted(Comparator.comparing(pc -> pc.getPlayer().getFullName(), String.CASE_INSENSITIVE_ORDER))
                    .toList();
        }

        if (award.getSeason() == null) {
            return List.of();
        }

        return playerContractRepository.findBySeasonIdAndStatus(award.getSeason().getId(), ACTIVE_CONTRACT).stream()
                .sorted(Comparator.comparing(pc -> pc.getPlayer().getFullName(), String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional
    public void save(Long awardId, AwardResult input) {
        Award award = findAwardById(awardId);
        AwardResult target = input.getId() != null ? findResultById(input.getId()) : new AwardResult();

        target.setAward(award);
        target.setRepresentative(input.getRepresentative());
        target.setBankAccount(input.getBankAccount());
        target.setBankName(input.getBankName());

        if (target.getPaymentStatus() == null) {
            target.setPaymentStatus(UNPAID_STATUS);
        }

        if (TEAM_AWARD.equals(award.getAwardType())) {
            Team team = resolveTeam(input);
            validateTeamAward(award, target.getId(), team);
            target.setTeam(team);
            target.setPlayerContract(null);
        } else if (INDIVIDUAL_AWARD.equals(award.getAwardType())) {
            PlayerContract playerContract = resolvePlayerContract(input);
            validateIndividualAward(award, target.getId(), playerContract);
            target.setPlayerContract(playerContract);
            target.setTeam(null);
        } else {
            throw new IllegalArgumentException("Lo\u1ea1i gi\u1ea3i th\u01b0\u1edfng kh\u00f4ng h\u1ee3p l\u1ec7!");
        }

        awardResultRepository.save(target);
    }

    @Transactional
    public void delete(Long resultId) {
        awardResultRepository.deleteById(resultId);
    }

    private Team resolveTeam(AwardResult input) {
        if (input.getTeam() == null || input.getTeam().getId() == null) {
            throw new IllegalArgumentException("B\u1ea1n ch\u01b0a ch\u1ecdn \u0111\u1ed9i nh\u1eadn gi\u1ea3i!");
        }

        Long teamId = input.getTeam().getId();
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("\u0110\u1ed9i b\u00f3ng kh\u00f4ng t\u1ed3n t\u1ea1i!"));
    }

    private PlayerContract resolvePlayerContract(AwardResult input) {
        if (input.getPlayerContract() == null || input.getPlayerContract().getId() == null) {
            throw new IllegalArgumentException("B\u1ea1n ch\u01b0a ch\u1ecdn c\u1ea7u th\u1ee7 nh\u1eadn gi\u1ea3i!");
        }

        Long contractId = input.getPlayerContract().getId();
        return playerContractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("H\u1ee3p \u0111\u1ed3ng c\u1ea7u th\u1ee7 kh\u00f4ng t\u1ed3n t\u1ea1i!"));
    }

    private void validateTeamAward(Award award, Long resultId, Team team) {
        if (award.getMatch() != null) {
            boolean presentInMatch = matchDetailRepository.findByMatchId(award.getMatch().getId()).stream()
                    .map(MatchDetail::getTeam)
                    .filter(Objects::nonNull)
                    .map(Team::getId)
                    .anyMatch(teamId -> Objects.equals(teamId, team.getId()));

            if (!presentInMatch) {
                throw new IllegalArgumentException("\u0110\u1ed9i nh\u1eadn gi\u1ea3i kh\u00f4ng thu\u1ed9c tr\u1eadn \u0111\u1ea5u \u0111\u01b0\u1ee3c ch\u1ecdn!");
            }
        }

        boolean exists = resultId == null
                ? awardResultRepository.existsByAwardIdAndTeamId(award.getId(), team.getId())
                : awardResultRepository.existsByAwardIdAndTeamIdAndIdNot(award.getId(), team.getId(), resultId);
        if (exists) {
            throw new IllegalArgumentException("\u0110\u1ed9i n\u00e0y \u0111\u00e3 \u0111\u01b0\u1ee3c g\u00e1n cho h\u1ea1ng m\u1ee5c gi\u1ea3i th\u01b0\u1edfng n\u00e0y!");
        }
    }

    private void validateIndividualAward(Award award, Long resultId, PlayerContract playerContract) {
        if (!ACTIVE_CONTRACT.equals(playerContract.getStatus())) {
            throw new IllegalArgumentException("C\u1ea7u th\u1ee7 \u0111\u01b0\u1ee3c ch\u1ecdn kh\u00f4ng c\u00f2n h\u1ee3p \u0111\u1ed3ng ho\u1ea1t \u0111\u1ed9ng!");
        }

        if (award.getSeason() != null
                && !Objects.equals(playerContract.getSeason().getId(), award.getSeason().getId())) {
            throw new IllegalArgumentException("C\u1ea7u th\u1ee7 kh\u00f4ng thu\u1ed9c m\u00f9a gi\u1ea3i c\u1ee7a h\u1ea1ng m\u1ee5c n\u00e0y!");
        }

        if (award.getMatch() != null) {
            List<MatchDetail> matchDetails = matchDetailRepository.findByMatchId(award.getMatch().getId());
            boolean inMatch = matchDetails.stream()
                    .map(MatchDetail::getTeam)
                    .filter(Objects::nonNull)
                    .map(Team::getId)
                    .anyMatch(teamId -> Objects.equals(teamId, playerContract.getTeam().getId()));
            Long matchSeasonId = award.getMatch().getRound().getSeason().getId();

            if (!inMatch || !Objects.equals(playerContract.getSeason().getId(), matchSeasonId)) {
                throw new IllegalArgumentException("C\u1ea7u th\u1ee7 kh\u00f4ng thu\u1ed9c tr\u1eadn \u0111\u1ea5u ho\u1eb7c m\u00f9a gi\u1ea3i c\u1ee7a h\u1ea1ng m\u1ee5c n\u00e0y!");
            }
        }

        boolean exists = resultId == null
                ? awardResultRepository.existsByAwardIdAndPlayerContractId(award.getId(), playerContract.getId())
                : awardResultRepository.existsByAwardIdAndPlayerContractIdAndIdNot(award.getId(), playerContract.getId(), resultId);
        if (exists) {
            throw new IllegalArgumentException("C\u1ea7u th\u1ee7 n\u00e0y \u0111\u00e3 \u0111\u01b0\u1ee3c g\u00e1n cho h\u1ea1ng m\u1ee5c gi\u1ea3i th\u01b0\u1edfng n\u00e0y!");
        }
    }
}
