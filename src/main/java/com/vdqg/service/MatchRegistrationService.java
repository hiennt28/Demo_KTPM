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

    private static final String ACTIVE_SEASON_STATUS = "\u0110ANG DI\u1EC4N RA";
    private static final String ACTIVE_CONTRACT_STATUS = "HO\u1EA0T \u0110\u1ED8NG";
    private static final String NORMAL_HEALTH_STATUS = "B\u00CCNH TH\u01AF\u1EDCNG";
    private static final String VIETNAMESE_NATIONALITY = "Vi\u1EC7t Nam";

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
                .orElseThrow(() -> new RuntimeException("Kh\u00f4ng t\u00ecm th\u1ea5y \u0111\u1ed9i tham gia tr\u1eadn \u0111\u1ea5u!"));
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
            throw new IllegalStateException("\u0110\u1ed9i b\u00f3ng n\u00e0y \u0111\u00e3 \u0111\u0103ng k\u00fd \u0111\u1ed9i h\u00ecnh cho tr\u1eadn n\u00e0y r\u1ed3i!");
        }

        if (startingContractIds == null || startingContractIds.size() != 11) {
            throw new IllegalArgumentException("Ph\u1ea3i ch\u1ecdn \u0111\u00fang 11 c\u1ea7u th\u1ee7 \u0111\u00e1 ch\u00ednh!");
        }

        List<Long> safeSubstituteIds = substituteContractIds != null
                ? substituteContractIds
                : Collections.emptyList();

        if (!Collections.disjoint(startingContractIds, safeSubstituteIds)) {
            throw new IllegalArgumentException("M\u1ed9t c\u1ea7u th\u1ee7 kh\u00f4ng th\u1ec3 v\u1eeba \u0111\u00e1 ch\u00ednh v\u1eeba d\u1ef1 b\u1ecb!");
        }

        Set<Long> selectedIds = new LinkedHashSet<>();
        selectedIds.addAll(startingContractIds);
        selectedIds.addAll(safeSubstituteIds);

        Map<Long, PlayerContract> contractsById = playerContractRepository.findAllById(selectedIds).stream()
                .collect(Collectors.toMap(
                        PlayerContract::getId,
                        contract -> contract,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        if (contractsById.size() != selectedIds.size()) {
            throw new IllegalArgumentException("C\u00f3 c\u1ea7u th\u1ee7 \u0111\u0103ng k\u00fd kh\u00f4ng t\u1ed3n t\u1ea1i trong h\u1ec7 th\u1ed1ng!");
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
                throw new IllegalArgumentException("Danh s\u00e1ch c\u1ea7u th\u1ee7 \u0111\u0103ng k\u00fd kh\u00f4ng thu\u1ed9c \u0111\u00fang \u0111\u1ed9i ho\u1eb7c \u0111\u00fang m\u00f9a gi\u1ea3i!");
            }
        }

        List<PlayerContract> startingContracts = startingContractIds.stream()
                .map(contractsById::get)
                .toList();

        for (PlayerContract contract : startingContracts) {
            if (contract.getRedCards() > 0 || contract.getYellowCards() >= 3) {
                throw new IllegalArgumentException(
                        "C\u1ea7u th\u1ee7 [" + contract.getPlayer().getFullName() + "] \u0111ang b\u1ecb \u0111\u00ecnh ch\u1ec9 thi \u0111\u1ea5u!");
            }
            if (!NORMAL_HEALTH_STATUS.equals(contract.getHealthStatus())) {
                throw new IllegalArgumentException(
                        "C\u1ea7u th\u1ee7 [" + contract.getPlayer().getFullName() + "] "
                                + "kh\u00f4ng \u0111\u1ee7 \u0111i\u1ec1u ki\u1ec7n th\u1ec3 l\u1ef1c (" + contract.getHealthStatus() + ")!");
            }
        }

        long foreignCount = startingContracts.stream()
                .filter(contract -> !VIETNAMESE_NATIONALITY.equals(contract.getPlayer().getNationality()))
                .count();
        if (foreignCount > 3) {
            throw new IllegalArgumentException(
                    "V\u01b0\u1ee3t gi\u1edbi h\u1ea1n ngo\u1ea1i binh! T\u1ed1i \u0111a 3 c\u1ea7u th\u1ee7 n\u01b0\u1edbc ngo\u00e0i \u0111\u00e1 ch\u00ednh. "
                            + "(Hi\u1ec7n ch\u1ecdn: " + foreignCount + ")");
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
