package com.vdqg.service;

import com.vdqg.dto.AwardResultFormOptions;
import com.vdqg.entity.Award;
import com.vdqg.entity.AwardResult;
import com.vdqg.entity.MatchDetail;
import com.vdqg.entity.Player;
import com.vdqg.entity.PlayerContract;
import com.vdqg.entity.Team;
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

    private static final String TEAM_AWARD = "TẬP THỂ";
    private static final String INDIVIDUAL_AWARD = "CÁ NHÂN";
    private static final String ACTIVE_CONTRACT = "HOẠT ĐỘNG";
    private static final String UNPAID_STATUS = "CHƯA THANH TOÁN";

    private final AwardService awardService;
    private final AwardResultRepository awardResultRepository;
    private final TeamRepository teamRepository;
    private final PlayerContractRepository playerContractRepository;
    private final MatchDetailRepository matchDetailRepository;

    public Award findAwardById(Long awardId) {
        return awardService.findById(awardId);
    }

    public AwardResult findResultById(Long resultId) {
        return awardResultRepository.findDetailedById(resultId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người nhận giải!"));
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

    public List<Player> getPlayerOptions(Award award) {
        return getEligibleContracts(award).stream()
                .map(PlayerContract::getPlayer)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.comparing(Player::getFullName, String.CASE_INSENSITIVE_ORDER))
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
            target.setPlayer(null);
        } else if (INDIVIDUAL_AWARD.equals(award.getAwardType())) {
            PlayerContract playerContract = resolveEligiblePlayerContract(award, input);
            validateIndividualAward(award, target.getId(), playerContract);
            target.setPlayer(playerContract.getPlayer());
            target.setTeam(null);
        } else {
            throw new IllegalArgumentException("Loại giải thưởng không hợp lệ!");
        }

        awardResultRepository.save(target);
    }

    @Transactional
    public void saveExisting(AwardResult awardResult) {
        awardResultRepository.save(awardResult);
    }

    @Transactional
    public void delete(Long resultId) {
        awardResultRepository.deleteById(resultId);
    }

    private Team resolveTeam(AwardResult input) {
        if (input.getTeam() == null || input.getTeam().getId() == null) {
            throw new IllegalArgumentException("Bạn chưa chọn đội nhận giải!");
        }

        return teamRepository.findById(input.getTeam().getId())
                .orElseThrow(() -> new IllegalArgumentException("Đội bóng không tồn tại!"));
    }

    private PlayerContract resolveEligiblePlayerContract(Award award, AwardResult input) {
        if (input.getPlayer() == null || input.getPlayer().getId() == null) {
            throw new IllegalArgumentException("Bạn chưa chọn cầu thủ nhận giải!");
        }

        Long playerId = input.getPlayer().getId();
        return getEligibleContracts(award).stream()
                .filter(contract -> contract.getPlayer() != null)
                .filter(contract -> Objects.equals(contract.getPlayer().getId(), playerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cầu thủ được chọn không hợp lệ với hạng mục này!"));
    }

    private List<PlayerContract> getEligibleContracts(Award award) {
        if (award.getMatch() != null) {
            List<Long> teamIds = matchDetailRepository.findByMatchId(award.getMatch().getId()).stream()
                    .map(MatchDetail::getTeam)
                    .filter(Objects::nonNull)
                    .map(Team::getId)
                    .toList();
            Long seasonId = award.getMatch().getRound().getSeason().getId();

            return playerContractRepository.findByTeamIdInAndSeasonIdAndStatus(teamIds, seasonId, ACTIVE_CONTRACT);
        }

        if (award.getSeason() == null) {
            return List.of();
        }

        return playerContractRepository.findBySeasonIdAndStatus(award.getSeason().getId(), ACTIVE_CONTRACT);
    }

    private void validateTeamAward(Award award, Long resultId, Team team) {
        if (award.getMatch() != null) {
            boolean presentInMatch = matchDetailRepository.findByMatchId(award.getMatch().getId()).stream()
                    .map(MatchDetail::getTeam)
                    .filter(Objects::nonNull)
                    .map(Team::getId)
                    .anyMatch(teamId -> Objects.equals(teamId, team.getId()));

            if (!presentInMatch) {
                throw new IllegalArgumentException("Đội nhận giải không thuộc trận đấu được chọn!");
            }
        }

        boolean exists = resultId == null
                ? awardResultRepository.existsByAwardIdAndTeamId(award.getId(), team.getId())
                : awardResultRepository.existsByAwardIdAndTeamIdAndIdNot(award.getId(), team.getId(), resultId);
        if (exists) {
            throw new IllegalArgumentException("Đội này đã được gán cho hạng mục giải thưởng này!");
        }
    }

    private void validateIndividualAward(Award award, Long resultId, PlayerContract playerContract) {
        if (!ACTIVE_CONTRACT.equals(playerContract.getStatus())) {
            throw new IllegalArgumentException("Cầu thủ được chọn không còn hợp đồng hoạt động!");
        }

        if (award.getSeason() != null
                && !Objects.equals(playerContract.getSeason().getId(), award.getSeason().getId())) {
            throw new IllegalArgumentException("Cầu thủ không thuộc mùa giải của hạng mục này!");
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
                throw new IllegalArgumentException("Cầu thủ không thuộc trận đấu hoặc mùa giải của hạng mục này!");
            }
        }

        Long playerId = playerContract.getPlayer().getId();
        boolean exists = resultId == null
                ? awardResultRepository.existsByAwardIdAndPlayerId(award.getId(), playerId)
                : awardResultRepository.existsByAwardIdAndPlayerIdAndIdNot(award.getId(), playerId, resultId);
        if (exists) {
            throw new IllegalArgumentException("Cầu thủ này đã được gán cho hạng mục giải thưởng này!");
        }
    }

    public AwardResultFormOptions getFormOptions(Award award) {
        return new AwardResultFormOptions(
                getTeamOptions(award),
                getPlayerOptions(award)
        );
    }
}
