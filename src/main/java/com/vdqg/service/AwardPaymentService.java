package com.vdqg.service;

import com.vdqg.entity.Award;
import com.vdqg.entity.AwardPayment;
import com.vdqg.entity.AwardResult;
import com.vdqg.entity.Match;
import com.vdqg.entity.Season;
import com.vdqg.repository.AwardPaymentRepository;
import com.vdqg.repository.MatchRepository;
import com.vdqg.repository.SeasonRepository;
import com.vdqg.util.MatchDisplayNameUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AwardPaymentService {

    private static final String UNKNOWN_TEAM = "Chưa xác định";
    private static final String PAID_STATUS = "ĐÃ THANH TOÁN";

    private final SeasonRepository seasonRepository;
    private final AwardService awardService;
    private final AwardResultService awardResultService;
    private final MatchRepository matchRepository;
    private final AwardPaymentRepository paymentRepository;
    private final MatchDisplayNameUtil matchDisplayNameUtil;

    public List<Season> getAllSeasons() {
        return seasonRepository.findAll();
    }

    public Season findSeasonById(Long seasonId) {
        return seasonRepository.findById(seasonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mùa giải!"));
    }

    public List<Award> getAwardsBySeason(Long seasonId) {
        Season season = findSeasonById(seasonId);
        return awardService.getAwardsBySeason(season.getSeasonName());
    }

    public List<Match> getMatchesBySeason(Long seasonId) {
        return matchRepository.findByRoundSeasonIdOrderByMatchDateAsc(seasonId);
    }

    public Match findMatchById(Long matchId) {
        return matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trận đấu!"));
    }

    public Map<Long, String> getMatchDisplayNames(List<Match> matches) {
        List<Long> matchIds = matches.stream()
                .map(Match::getId)
                .filter(Objects::nonNull)
                .toList();
        return matchDisplayNameUtil.buildDisplayNames(matchIds);
    }

    public String getMatchDisplayName(Long matchId) {
        return matchDisplayNameUtil.buildDisplayNames(List.of(matchId))
                .getOrDefault(matchId, UNKNOWN_TEAM);
    }

    public List<Award> getAwardsByMatch(Long matchId) {
        return awardService.getAwardsByMatch(matchId);
    }

    public Award findAwardById(Long awardId) {
        return awardService.findById(awardId);
    }

    public List<AwardResult> getResultsByAward(Long awardId) {
        return awardResultService.getResultsByAward(awardId);
    }

    public AwardResult findResultById(Long id) {
        return awardResultService.findResultById(id);
    }

    @Transactional
    public AwardPayment processPayment(Long awardResultId,
                                       String paymentMethod,
                                       String note) {
        AwardResult result = findResultById(awardResultId);

        if (PAID_STATUS.equals(result.getPaymentStatus())
                || paymentRepository.existsByAwardResultId(awardResultId)) {
            throw new IllegalStateException("Giải thưởng này đã được thanh toán trước đó!");
        }

        AwardPayment payment = new AwardPayment();
        payment.setAwardResult(result);
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(result.getAward().getPrizeAmount());
        payment.setPaidAt(LocalDateTime.now());
        payment.setNote(note);
        paymentRepository.save(payment);

        result.setPaymentStatus(PAID_STATUS);
        awardResultService.saveExisting(result);

        return payment;
    }
}
