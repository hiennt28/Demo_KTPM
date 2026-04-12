package com.vdqg.service;

import com.vdqg.entity.Award;
import com.vdqg.entity.AwardPayment;
import com.vdqg.entity.AwardResult;
import com.vdqg.entity.Match;
import com.vdqg.entity.Season;
import com.vdqg.repository.AwardPaymentRepository;
import com.vdqg.repository.AwardRepository;
import com.vdqg.repository.AwardResultRepository;
import com.vdqg.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AwardPaymentService {

    private static final String UNKNOWN_TEAM = "Ch\u01b0a x\u00e1c \u0111\u1ecbnh";
    private static final String ACTIVE_AWARD_STATUS = "\u0110ANG \u00c1P D\u1ee4NG";
    private static final String PAID_STATUS = "\u0110\u00c3 THANH TO\u00c1N";

    private final AwardService awardService;
    private final AwardRepository awardRepository;
    private final AwardResultRepository awardResultRepository;
    private final AwardPaymentRepository paymentRepository;
    private final MatchRepository matchRepository;

    public List<Season> getAllSeasons() {
        return awardService.getSeasonOptions();
    }

    public Season findSeasonById(Long seasonId) {
        return awardService.findSeasonById(seasonId);
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
                .orElseThrow(() -> new RuntimeException("Kh\u00f4ng t\u00ecm th\u1ea5y tr\u1eadn \u0111\u1ea5u!"));
    }

    public Map<Long, String> getMatchDisplayNames(List<Match> matches) {
        return awardService.getDisplayNamesForMatches(matches);
    }

    public String getMatchDisplayName(Long matchId) {
        Match match = findMatchById(matchId);
        return awardService.getDisplayNamesForMatches(List.of(match))
                .getOrDefault(matchId, UNKNOWN_TEAM);
    }

    public List<Award> getAwardsByMatch(Long matchId) {
        return awardRepository.findByMatchIdAndStatus(matchId, ACTIVE_AWARD_STATUS);
    }

    public Award findAwardById(Long awardId) {
        return awardService.findById(awardId);
    }

    public List<AwardResult> getResultsByAward(Long awardId) {
        return awardResultRepository.findByAwardId(awardId);
    }

    public AwardResult findResultById(Long id) {
        return awardResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kh\u00f4ng t\u00ecm th\u1ea5y k\u1ebft qu\u1ea3 gi\u1ea3i th\u01b0\u1edfng!"));
    }

    @Transactional
    public AwardPayment processPayment(Long awardResultId,
                                       String paymentMethod,
                                       String note) {
        AwardResult result = findResultById(awardResultId);

        if (PAID_STATUS.equals(result.getPaymentStatus())
                || paymentRepository.existsByAwardResultId(awardResultId)) {
            throw new IllegalStateException("Gi\u1ea3i th\u01b0\u1edfng n\u00e0y \u0111\u00e3 \u0111\u01b0\u1ee3c thanh to\u00e1n tr\u01b0\u1edbc \u0111\u00f3!");
        }

        AwardPayment payment = new AwardPayment();
        payment.setAwardResult(result);
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(result.getAward().getPrizeAmount());
        payment.setPaidAt(LocalDateTime.now());
        payment.setNote(note);
        paymentRepository.save(payment);

        result.setPaymentStatus(PAID_STATUS);
        awardResultRepository.save(result);

        return payment;
    }
}
