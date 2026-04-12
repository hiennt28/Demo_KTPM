package com.vdqg.service;

import com.vdqg.entity.Award;
import com.vdqg.entity.Match;
import com.vdqg.entity.MatchDetail;
import com.vdqg.entity.Season;
import com.vdqg.repository.AwardRepository;
import com.vdqg.repository.MatchDetailRepository;
import com.vdqg.repository.MatchRepository;
import com.vdqg.repository.SeasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AwardService {

    private static final String UNKNOWN_TEAM = "Ch\u01b0a x\u00e1c \u0111\u1ecbnh";
    private static final String ACTIVE_AWARD_STATUS = "\u0110ANG \u00c1P D\u1ee4NG";
    private static final String CANCELED_AWARD_STATUS = "H\u1ee6Y \u00c1P D\u1ee4NG";
    private static final String SEASON_SCOPE = "M\u00d9A GI\u1ea2I";
    private static final String HOME_ROLE = "NH\u00c0";
    private static final String AWAY_ROLE = "KH\u00c1CH";

    private final AwardRepository awardRepository;
    private final MatchRepository matchRepository;
    private final MatchDetailRepository matchDetailRepository;
    private final SeasonRepository seasonRepository;

    public List<Award> getAllActiveAwards() {
        return awardRepository.findByStatus(ACTIVE_AWARD_STATUS);
    }

    public List<Award> getAwardsBySeason(String seasonName) {
        return awardRepository.findBySeasonSeasonNameAndStatus(seasonName, ACTIVE_AWARD_STATUS);
    }

    public List<Season> getSeasonOptions() {
        return seasonRepository.findAll().stream()
                .filter(season -> season.getSeasonName() != null)
                .sorted((left, right) -> left.getSeasonName().compareTo(right.getSeasonName()))
                .toList();
    }

    public Season findSeasonById(Long id) {
        return seasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kh\u00f4ng t\u00ecm th\u1ea5y m\u00f9a gi\u1ea3i ID: " + id));
    }

    public Award findById(Long id) {
        return awardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kh\u00f4ng t\u00ecm th\u1ea5y gi\u1ea3i th\u01b0\u1edfng ID: " + id));
    }

    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getDisplayNamesForMatches(List<Match> matches) {
        List<Long> matchIds = matches.stream()
                .map(Match::getId)
                .filter(Objects::nonNull)
                .toList();

        return buildMatchDisplayNames(matchIds);
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getDisplayNamesForAwards(List<Award> awards) {
        List<Long> matchIds = awards.stream()
                .map(Award::getMatch)
                .filter(Objects::nonNull)
                .map(Match::getId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        return buildMatchDisplayNames(matchIds);
    }

    @Transactional
    public void save(Award award) {
        if (award.getSeason() != null && award.getSeason().getId() != null) {
            Long seasonId = award.getSeason().getId();
            award.setSeason(seasonRepository.findById(seasonId)
                    .orElseThrow(() -> new IllegalArgumentException("M\u00f9a gi\u1ea3i kh\u00f4ng t\u1ed3n t\u1ea1i!")));
        } else {
            award.setSeason(null);
        }

        if (award.getMatch() != null && award.getMatch().getId() != null) {
            Long matchId = award.getMatch().getId();
            award.setMatch(matchRepository.findById(matchId)
                    .orElseThrow(() -> new IllegalArgumentException("Tr\u1eadn \u0111\u1ea5u kh\u00f4ng t\u1ed3n t\u1ea1i!")));
        } else {
            award.setMatch(null);
        }

        if (award.getId() == null
                && awardRepository.existsByAwardNameAndStatus(award.getAwardName(), ACTIVE_AWARD_STATUS)) {
            throw new IllegalArgumentException("T\u00ean gi\u1ea3i th\u01b0\u1edfng \u0111\u00e3 t\u1ed3n t\u1ea1i!");
        }
        if (award.getId() != null
                && awardRepository.existsByAwardNameAndStatusAndIdNot(
                award.getAwardName(), ACTIVE_AWARD_STATUS, award.getId())) {
            throw new IllegalArgumentException("T\u00ean gi\u1ea3i th\u01b0\u1edfng \u0111\u00e3 t\u1ed3n t\u1ea1i!");
        }

        if (SEASON_SCOPE.equals(award.getScope())) {
            award.setMatch(null);
        } else {
            award.setSeason(null);
        }

        awardRepository.save(award);
    }

    @Transactional
    public void softDelete(Long id) {
        Award award = findById(id);
        award.setStatus(CANCELED_AWARD_STATUS);
        awardRepository.save(award);
    }

    private Map<Long, String> buildMatchDisplayNames(List<Long> matchIds) {
        Map<Long, String> displayNames = new LinkedHashMap<>();
        if (matchIds.isEmpty()) {
            return displayNames;
        }

        Map<Long, List<MatchDetail>> detailsByMatchId = matchDetailRepository.findByMatchIdIn(matchIds).stream()
                .collect(Collectors.groupingBy(
                        detail -> detail.getMatch().getId(),
                        LinkedHashMap::new,
                        Collectors.toCollection(ArrayList::new)
                ));

        for (Long matchId : matchIds) {
            displayNames.put(matchId, buildMatchDisplayName(detailsByMatchId.get(matchId)));
        }

        return displayNames;
    }

    private String buildMatchDisplayName(List<MatchDetail> matchDetails) {
        String homeTeam = getTeamNameByRole(matchDetails, HOME_ROLE, 0);
        String awayTeam = getTeamNameByRole(matchDetails, AWAY_ROLE, 1);
        return homeTeam + " vs " + awayTeam;
    }

    private String getTeamNameByRole(List<MatchDetail> matchDetails, String role, int fallbackIndex) {
        if (matchDetails == null || matchDetails.isEmpty()) {
            return UNKNOWN_TEAM;
        }

        for (MatchDetail detail : matchDetails) {
            if (detail != null
                    && Objects.equals(role, detail.getRole())
                    && detail.getTeam() != null
                    && detail.getTeam().getTeamName() != null) {
                return detail.getTeam().getTeamName();
            }
        }

        if (fallbackIndex >= 0 && fallbackIndex < matchDetails.size()) {
            MatchDetail detail = matchDetails.get(fallbackIndex);
            if (detail != null && detail.getTeam() != null && detail.getTeam().getTeamName() != null) {
                return detail.getTeam().getTeamName();
            }
        }

        return UNKNOWN_TEAM;
    }
}
