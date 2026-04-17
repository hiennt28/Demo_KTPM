package com.vdqg.service;

import com.vdqg.entity.Match;
import com.vdqg.entity.MatchDetail;
import com.vdqg.entity.MatchRegistration;
import com.vdqg.entity.PlayerContract;
import com.vdqg.entity.RegistrationDetail;
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
    private static final String UNKNOWN_LABEL = "Chưa xác định";

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

    public Map<Long, String> getMatchDisplayNamesByRound(Long roundId) {
        return buildDisplayNames(getMatchesByRound(roundId));
    }

    public MatchDetail findMatchDetailById(Long id) {
        return matchDetailRepository.findWithTeamById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đội tham gia trận đấu!"));
    }

    public Match findMatchByDetailId(Long matchDetailId) {
        return matchRepository.findByMatchDetailsId(matchDetailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trận đấu của đội đăng ký!"));
    }

    public List<MatchDetail> getMatchDetails(Long matchId) {
        return matchDetailRepository.findByMatchId(matchId);
    }

    public String getMatchDisplayName(Long matchId) {
        return matchRepository.findById(matchId)
                .map(this::buildDisplayName)
                .orElse(UNKNOWN_LABEL);
    }

    public List<PlayerContract> getPlayerContractsForMatchDetail(Long matchDetailId) {
        MatchDetail matchDetail = findMatchDetailById(matchDetailId);
        Match match = findMatchByDetailId(matchDetailId);
        Long teamId = matchDetail.getTeam().getId();
        Long seasonId = getSeasonIdForMatch(match.getId());

        return playerContractRepository.findByTeamIdAndSeasonIdAndStatus(teamId, seasonId, ACTIVE_CONTRACT_STATUS);
    }

    public Long getSeasonIdForMatchDetail(Long matchDetailId) {
        Match match = findMatchByDetailId(matchDetailId);
        return getSeasonIdForMatch(match.getId());
    }

    public Long getRoundIdForMatchDetail(Long matchDetailId) {
        Match match = findMatchByDetailId(matchDetailId);
        return roundRepository.findByMatchesId(match.getId())
                .map(Round::getId)
                .orElse(null);
    }

    public List<String> getPlayingPositionOptions() {
        return List.of(
                "Thủ môn",
                "Trung vệ",
                "Hậu vệ phải",
                "Hậu vệ trái",
                "Tiền vệ phòng ngự",
                "Tiền vệ trung tâm",
                "Tiền vệ",
                "Tiền vệ cánh",
                "Tiền vệ tấn công",
                "Tiền đạo cánh",
                "Tiền đạo"
        );
    }

    @Transactional
    public void registerPlayers(Long matchDetailId,
                                List<Long> startingContractIds,
                                List<Long> substituteContractIds,
                                Map<Long, String> playingPositions) {
        MatchDetail matchDetail = findMatchDetailById(matchDetailId);
        Match match = findMatchByDetailId(matchDetailId);

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
                .collect(Collectors.toMap(
                        PlayerContract::getId,
                        contract -> contract,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        if (contractsById.size() != selectedIds.size()) {
            throw new IllegalArgumentException("Có cầu thủ đăng ký không tồn tại trong hệ thống!");
        }

        Long expectedTeamId = matchDetail.getTeam().getId();
        Long expectedSeasonId = getSeasonIdForMatch(match.getId());

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
                        "Cầu thủ [" + contract.getPlayer().getFullName() + "] không đủ điều kiện thể lực ("
                                + contract.getHealthStatus() + ")!");
            }
        }

        long foreignCount = startingContracts.stream()
                .filter(this::isForeignPlayer)
                .count();
        if (foreignCount > 3) {
            throw new IllegalArgumentException(
                    "Vượt giới hạn ngoại binh! Tối đa 3 cầu thủ nước ngoài đá chính. (Hiện chọn: " + foreignCount + ")");
        }

        MatchRegistration registration = new MatchRegistration();
        registration.setMatchDetail(matchDetail);

        for (Long contractId : startingContractIds) {
            PlayerContract contract = contractsById.get(contractId);
            RegistrationDetail detail = new RegistrationDetail();
            detail.setPlayerContract(contract);
            detail.setRole(RegistrationDetail.STARTING_ROLE);
            detail.setPlayingPosition(resolvePlayingPosition(contract, playingPositions));
            registration.getRegistrationDetails().add(detail);
        }

        for (Long contractId : safeSubstituteIds) {
            PlayerContract contract = contractsById.get(contractId);
            RegistrationDetail detail = new RegistrationDetail();
            detail.setPlayerContract(contract);
            detail.setRole(RegistrationDetail.SUBSTITUTE_ROLE);
            detail.setPlayingPosition(resolvePlayingPosition(contract, playingPositions));
            registration.getRegistrationDetails().add(detail);
        }

        registrationRepository.save(registration);
    }

    private String resolvePlayingPosition(PlayerContract contract, Map<Long, String> playingPositions) {
        if (contract == null || contract.getId() == null) {
            throw new IllegalArgumentException("Không xác định được cầu thủ đăng ký!");
        }

        String selectedPosition = playingPositions != null ? playingPositions.get(contract.getId()) : null;
        if (selectedPosition != null && !selectedPosition.isBlank()) {
            return selectedPosition.trim();
        }

        String preferredPosition = contract.getPlayer() != null ? contract.getPlayer().getPreferredPosition() : null;
        if (preferredPosition != null && !preferredPosition.isBlank()) {
            return preferredPosition;
        }

        throw new IllegalArgumentException(
                "Bạn phải chọn vị trí thi đấu cho cầu thủ [" + contract.getPlayer().getFullName() + "]!");
    }

    private Long getSeasonIdForMatch(Long matchId) {
        return seasonRepository.findByMatchId(matchId)
                .map(Season::getId)
                .orElseThrow(() -> new IllegalArgumentException("Không xác định được mùa giải của trận đấu!"));
    }

    private boolean isForeignPlayer(PlayerContract contract) {
        if (contract.getIsForeign() != null) {
            return contract.getIsForeign();
        }

        return contract.getPlayer() != null
                && contract.getPlayer().getNationality() != null
                && !Objects.equals(contract.getPlayer().getNationality(), "Việt Nam");
    }

    private Map<Long, String> buildDisplayNames(List<Match> matches) {
        Map<Long, String> result = new LinkedHashMap<>();
        for (Match match : matches) {
            if (match == null || match.getId() == null) {
                continue;
            }
            result.put(match.getId(), buildDisplayName(match));
        }
        return result;
    }

    private String buildDisplayName(Match match) {
        List<MatchDetail> details = match.getMatchDetails() != null ? match.getMatchDetails() : List.of();
        String homeTeam = resolveTeamName(details, "NHÀ", 0);
        String awayTeam = resolveTeamName(details, "KHÁCH", 1);
        return homeTeam + " vs " + awayTeam;
    }

    private String resolveTeamName(List<MatchDetail> details, String role, int fallbackIndex) {
        for (MatchDetail detail : details) {
            if (detail != null
                    && Objects.equals(role, detail.getRole())
                    && detail.getTeam() != null
                    && detail.getTeam().getTeamName() != null) {
                return detail.getTeam().getTeamName();
            }
        }

        if (fallbackIndex >= 0 && fallbackIndex < details.size()) {
            MatchDetail detail = details.get(fallbackIndex);
            if (detail != null && detail.getTeam() != null && detail.getTeam().getTeamName() != null) {
                return detail.getTeam().getTeamName();
            }
        }

        return UNKNOWN_LABEL;
    }
}
