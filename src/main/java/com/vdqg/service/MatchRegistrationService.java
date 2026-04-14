package com.vdqg.service;

import com.vdqg.entity.Match;
import com.vdqg.entity.MatchDetail;
import com.vdqg.entity.MatchRegistration;
import com.vdqg.entity.PlayerContract;
import com.vdqg.entity.Round;
import com.vdqg.entity.Season;
import com.vdqg.repository.MatchDetailRepository;
import com.vdqg.repository.MatchRegistrationRepository;
import com.vdqg.repository.MatchRepository;
import com.vdqg.repository.PlayerContractRepository;
import com.vdqg.repository.RoundRepository;
import com.vdqg.repository.SeasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchRegistrationService {

    private static final String ACTIVE_SEASON_STATUS = "ĐANG DIỄN RA";
    private static final String ACTIVE_CONTRACT_STATUS = "HOẠT ĐỘNG";
    private static final String NORMAL_HEALTH_STATUS = "BÌNH THƯỜNG";
    private static final String VIETNAMESE_NATIONALITY = "Việt Nam";

    private final SeasonRepository seasonRepository;
    private final RoundRepository roundRepository;
    private final MatchRepository matchRepository;
    private final MatchDetailRepository matchDetailRepository;
    private final PlayerContractRepository playerContractRepository;
    private final MatchRegistrationRepository registrationRepository;

    public List<Season> getActiveSeasons() {
        return seasonRepository.findByStatus(ACTIVE_SEASON_STATUS);
    }

    public List<Round> getRoundsBySeason(Long seasonId) {
        return roundRepository.findBySeasonIdOrderByRoundOrder(seasonId);
    }

    public List<Match> getMatchesByRound(Long roundId) {
        return matchRepository.findByRoundId(roundId);
    }

    public MatchDetail findMatchDetailById(Long id) {
        return matchDetailRepository.findDetailedById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đội tham gia trận đấu!"));
    }

    public List<MatchDetail> getMatchDetails(Long matchId) {
        return matchDetailRepository.findByMatchId(matchId);
    }

    public List<PlayerContract> getPlayerContractsForMatchDetail(Long matchDetailId) {
        MatchDetail matchDetail = findMatchDetailById(matchDetailId);
        Long teamId = matchDetail.getTeam().getId();
        Long seasonId = matchDetail.getMatch().getRound().getSeason().getId();

        return playerContractRepository.findByTeamIdAndSeasonIdAndStatus(teamId, seasonId, ACTIVE_CONTRACT_STATUS);
    }

    @Transactional
    public void registerPlayers(Long matchDetailId,
                                List<Long> startingContractIds,
                                List<Long> substituteContractIds) {

        MatchDetail matchDetail = findMatchDetailById(matchDetailId);
        if (registrationRepository.existsByMatchDetailId(matchDetailId)) {
            throw new IllegalStateException("Đội bóng này đã đăng ký đội hình cho trận này rồi!");
        }

        if (startingContractIds == null || startingContractIds.size() != 11) {
            throw new IllegalArgumentException("Phải chọn đúng 11 cầu thủ đá chính!");
        }

        List<Long> safeSubstituteIds = substituteContractIds != null
                ? substituteContractIds
                : Collections.emptyList();

        if (!Collections.disjoint(startingContractIds, safeSubstituteIds)) {
            throw new IllegalArgumentException("Một cầu thủ không thể vừa đá chính vừa dự bị!");
        }

        Set<Long> selectedIds = new LinkedHashSet<>();
        selectedIds.addAll(startingContractIds);
        selectedIds.addAll(safeSubstituteIds);

        Map<Long, PlayerContract> contractsById = playerContractRepository.findAllById(selectedIds).stream()
                .collect(Collectors.toMap(PlayerContract::getId, contract -> contract,
                        (left, right) -> left, LinkedHashMap::new));

        if (contractsById.size() != selectedIds.size()) {
            throw new IllegalArgumentException("Có cầu thủ đăng ký không tồn tại trong hệ thống!");
        }

        Long expectedTeamId = matchDetail.getTeam().getId();
        Long expectedSeasonId = matchDetail.getMatch().getRound().getSeason().getId();

        for (Long contractId : selectedIds) {
            PlayerContract contract = contractsById.get(contractId);
            if (contract == null
                    || contract.getTeam() == null
                    || contract.getSeason() == null
                    || !Objects.equals(contract.getTeam().getId(), expectedTeamId)
                    || !Objects.equals(contract.getSeason().getId(), expectedSeasonId)
                    || !ACTIVE_CONTRACT_STATUS.equals(contract.getStatus())) {
                throw new IllegalArgumentException("Danh sách cầu thủ đăng ký không thuộc đúng đội hoặc đúng mùa giải!");
            }
        }

        List<PlayerContract> startingContracts = startingContractIds.stream()
                .map(contractsById::get)
                .toList();

        for (PlayerContract contract : startingContracts) {
            if (contract.getRedCards() > 0 || contract.getYellowCards() >= 3) {
                throw new IllegalArgumentException(
                        "Cầu thủ [" + contract.getPlayer().getFullName() + "] đang bị đình chỉ thi đấu!");
            }
            if (!NORMAL_HEALTH_STATUS.equals(contract.getHealthStatus())) {
                throw new IllegalArgumentException(
                        "Cầu thủ [" + contract.getPlayer().getFullName() + "] "
                                + "không đủ điều kiện thể lực (" + contract.getHealthStatus() + ")!");
            }
        }

        long foreignCount = startingContracts.stream()
                .filter(contract -> !VIETNAMESE_NATIONALITY.equals(contract.getPlayer().getNationality()))
                .count();
        if (foreignCount > 3) {
            throw new IllegalArgumentException(
                    "Vượt giới hạn ngoại binh! Tối đa 3 cầu thủ nước ngoài đá chính. "
                            + "(Hiện chọn: " + foreignCount + ")");
        }

        MatchRegistration registration = new MatchRegistration();
        registration.setMatchDetail(matchDetail);
        registration.setStartingPlayerIds(
                startingContractIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
        registration.setSubstitutePlayerIds(
                safeSubstituteIds.stream().map(String::valueOf).collect(Collectors.joining(",")));

        registrationRepository.save(registration);
    }
}
