package com.vdqg.service;

import com.vdqg.dto.AwardFormOptions;
import com.vdqg.entity.Award;
import com.vdqg.entity.Match;
import com.vdqg.entity.Season;
import com.vdqg.repository.AwardRepository;
import com.vdqg.repository.MatchRepository;
import com.vdqg.repository.SeasonRepository;
import com.vdqg.util.MatchDisplayNameUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AwardService {

    private static final String ACTIVE_AWARD_STATUS = "ĐANG ÁP DỤNG";
    private static final String CANCELED_AWARD_STATUS = "HỦY ÁP DỤNG";
    private static final String SEASON_SCOPE = "MÙA GIẢI";

    private final AwardRepository awardRepository;
    private final MatchRepository matchRepository;
    private final SeasonRepository seasonRepository;
    private final MatchDisplayNameUtil matchDisplayNameUtil;

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
                .toList();

        return matchDisplayNameUtil.buildDisplayNames(matchIds);
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

        return matchDisplayNameUtil.buildDisplayNames(matchIds);
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

    public AwardFormOptions getFormOptions() {
        List<Season> seasons = getSeasonOptions();
        List<Match> matches = getAllMatches();
        Map<Long, String> displayNames = getDisplayNamesForMatches(matches);
        return new AwardFormOptions(seasons, matches, displayNames);
    }
}
