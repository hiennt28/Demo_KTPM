package com.vdqg.service;

import com.vdqg.entity.Award;
import com.vdqg.entity.AwardResult;
import com.vdqg.entity.MatchDetail;
import com.vdqg.entity.Player;
import com.vdqg.entity.PlayerContract;
import com.vdqg.entity.Season;
import com.vdqg.entity.Team;
import com.vdqg.repository.AwardRepository;
import com.vdqg.repository.AwardResultRepository;
import com.vdqg.repository.MatchDetailRepository;
import com.vdqg.repository.PlayerContractRepository;
import com.vdqg.repository.SeasonRepository;
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
    private static final String PAID_STATUS = "ĐÃ THANH TOÁN";
    private static final String LEGACY_TEAM_AWARD = "Táº¬P THá»‚";
    private static final String LEGACY_INDIVIDUAL_AWARD = "CÃ NHÃ‚N";
    private static final String LEGACY_ACTIVE_CONTRACT = "HOáº T Äá»˜NG";
    private static final String LEGACY_PAID_STATUS = "ÄÃƒ THANH TOÃN";

    private final AwardRepository awardRepository;
    private final AwardResultRepository awardResultRepository;
    private final TeamRepository teamRepository;
    private final PlayerContractRepository playerContractRepository;
    private final MatchDetailRepository matchDetailRepository;
    private final SeasonRepository seasonRepository;

    public Award findAwardById(Long awardId) {
        return awardRepository.findById(awardId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giải thưởng!"));
    }

    public AwardResult findResultById(Long resultId) {
        return awardResultRepository.findDetailedById(resultId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người nhận giải!"));
    }

    public AwardResult findEditableResultById(Long resultId) {
        AwardResult result = findResultById(resultId);
        ensureEditable(result);
        return result;
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
        AwardResult target = input.getId() != null ? findEditableResultById(input.getId()) : new AwardResult();

        target.setAward(award);
        target.setRepresentative(input.getRepresentative());
        target.setBankAccount(input.getBankAccount());
        target.setBankName(input.getBankName());

        if (target.getPaymentStatus() == null) {
            target.setPaymentStatus(UNPAID_STATUS);
        }

        if (isTeamAward(award.getAwardType())) {
            Team team = resolveTeam(input);
            validateTeamAward(award, target.getId(), team);
            target.setTeam(team);
            target.setPlayer(null);
        } else if (isIndividualAward(award.getAwardType())) {
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
    public void delete(Long resultId) {
        AwardResult result = findEditableResultById(resultId);
        awardResultRepository.delete(result);
    }

    private void ensureEditable(AwardResult result) {
        if (PAID_STATUS.equals(result.getPaymentStatus())) {
            throw new IllegalStateException("Người nhận giải đã thanh toán nên không thể chỉnh sửa hoặc xóa.");
        }
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
            Long seasonId = getSeasonIdForMatch(award.getMatch().getId());

            List<PlayerContract> activeContracts =
                    playerContractRepository.findByTeamIdInAndSeasonIdAndStatus(teamIds, seasonId, ACTIVE_CONTRACT);
            return !activeContracts.isEmpty()
                    ? activeContracts
                    : getLegacyOrAllContractsByTeams(teamIds, seasonId);
        }

        if (award.getSeason() == null) {
            return List.of();
        }

        List<PlayerContract> activeContracts =
                playerContractRepository.findBySeasonIdAndStatus(award.getSeason().getId(), ACTIVE_CONTRACT);
        return !activeContracts.isEmpty()
                ? activeContracts
                : getLegacyOrAllContractsBySeason(award.getSeason().getId());
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
            Long matchSeasonId = getSeasonIdForMatch(award.getMatch().getId());

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

    private Long getSeasonIdForMatch(Long matchId) {
        return seasonRepository.findByMatchId(matchId)
                .map(Season::getId)
                .orElseThrow(() -> new IllegalArgumentException("Không xác định được mùa giải của trận đấu!"));
    }

    private boolean isTeamAward(String awardType) {
        return TEAM_AWARD.equals(awardType) || LEGACY_TEAM_AWARD.equals(awardType);
    }

    private boolean isIndividualAward(String awardType) {
        return INDIVIDUAL_AWARD.equals(awardType) || LEGACY_INDIVIDUAL_AWARD.equals(awardType);
    }

    private List<PlayerContract> getLegacyOrAllContractsByTeams(List<Long> teamIds, Long seasonId) {
        List<PlayerContract> legacyContracts =
                playerContractRepository.findByTeamIdInAndSeasonIdAndStatus(teamIds, seasonId, LEGACY_ACTIVE_CONTRACT);
        return !legacyContracts.isEmpty()
                ? legacyContracts
                : playerContractRepository.findByTeamIdInAndSeasonId(teamIds, seasonId);
    }

    private List<PlayerContract> getLegacyOrAllContractsBySeason(Long seasonId) {
        List<PlayerContract> legacyContracts =
                playerContractRepository.findBySeasonIdAndStatus(seasonId, LEGACY_ACTIVE_CONTRACT);
        return !legacyContracts.isEmpty()
                ? legacyContracts
                : playerContractRepository.findBySeasonId(seasonId);
    }
}
