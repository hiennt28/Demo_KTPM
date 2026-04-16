package com.vdqg.service;

import com.vdqg.entity.Award;
import com.vdqg.entity.Match;
import com.vdqg.entity.MatchDetail;
import com.vdqg.entity.Season;
import com.vdqg.repository.AwardRepository;
import com.vdqg.repository.MatchRepository;
import com.vdqg.repository.SeasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AwardService {

    private static final String ACTIVE_AWARD_STATUS = "ĐANG ÁP DỤNG";
    private static final String CANCELED_AWARD_STATUS = "HỦY ÁP DỤNG";
    private static final String SEASON_SCOPE = "MÙA GIẢI";
    private static final String MATCH_SCOPE = "TRẬN ĐẤU";

    private final AwardRepository awardRepository;
    private final MatchRepository matchRepository;
    private final SeasonRepository seasonRepository;

    public List<Award> getAllActiveAwards() {
        return awardRepository.findByStatus(ACTIVE_AWARD_STATUS);
    }

    public List<Award> getAwardsBySeason(String seasonName) {
        return awardRepository.findBySeasonSeasonNameAndStatus(seasonName, ACTIVE_AWARD_STATUS);
    }

    public List<Award> getAwardsByMatch(Long matchId) {
        return awardRepository.findByMatchIdAndStatus(matchId, ACTIVE_AWARD_STATUS);
    }

    public List<Season> getSeasonOptions() {
        return seasonRepository.findAll().stream()
                .filter(season -> season.getSeasonName() != null)
                .sorted((left, right) -> left.getSeasonName().compareTo(right.getSeasonName()))
                .toList();
    }

    public Season findSeasonById(Long id) {
        return seasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mùa giải ID: " + id));
    }

    public Award findById(Long id) {
        return awardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giải thưởng ID: " + id));
    }

    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getDisplayNamesForMatches(List<Match> matches) {
        List<Long> matchIds = matches.stream()
                .map(Match::getId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (matchIds.isEmpty()) {
            return Map.of();
        }
        return buildDisplayNames(matchRepository.findByIdIn(matchIds));
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
        if (matchIds.isEmpty()) {
            return Map.of();
        }
        return buildDisplayNames(matchRepository.findByIdIn(matchIds));
    }

    @Transactional
    public void save(Award award) {
        if (award.getSeason() != null && award.getSeason().getId() != null) {
            Long seasonId = award.getSeason().getId();
            award.setSeason(seasonRepository.findById(seasonId)
                    .orElseThrow(() -> new IllegalArgumentException("Mùa giải không tồn tại!")));
        } else {
            award.setSeason(null);
        }

        if (award.getMatch() != null && award.getMatch().getId() != null) {
            Long matchId = award.getMatch().getId();
            award.setMatch(matchRepository.findById(matchId)
                    .orElseThrow(() -> new IllegalArgumentException("Trận đấu không tồn tại!")));
        } else {
            award.setMatch(null);
        }

        if (award.getId() == null
                && awardRepository.existsByAwardNameAndStatus(award.getAwardName(), ACTIVE_AWARD_STATUS)) {
            throw new IllegalArgumentException("Tên giải thưởng đã tồn tại!");
        }
        if (award.getId() != null
                && awardRepository.existsByAwardNameAndStatusAndIdNot(
                award.getAwardName(), ACTIVE_AWARD_STATUS, award.getId())) {
            throw new IllegalArgumentException("Tên giải thưởng đã tồn tại!");
        }

        if (SEASON_SCOPE.equals(award.getScope())) {
            if (award.getSeason() == null) {
                throw new IllegalArgumentException("Bạn phải chọn mùa giải cho giải thưởng theo mùa!");
            }
            award.setMatch(null);
        } else if (MATCH_SCOPE.equals(award.getScope())) {
            if (award.getMatch() == null) {
                throw new IllegalArgumentException("Bạn phải chọn trận đấu cho giải thưởng theo trận!");
            }
            award.setSeason(null);
        } else {
            throw new IllegalArgumentException("Phạm vi áp dụng không hợp lệ!");
        }

        award.setStatus(ACTIVE_AWARD_STATUS);
        awardRepository.save(award);
    }

    @Transactional
    public void softDelete(Long id) {
        Award award = findById(id);
        award.setStatus(CANCELED_AWARD_STATUS);
        awardRepository.save(award);
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
        if (match == null) {
            return "Chưa xác định vs Chưa xác định";
        }

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

        return "Chưa xác định";
    }
}
