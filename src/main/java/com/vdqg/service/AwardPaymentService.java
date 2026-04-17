package com.vdqg.service;

import com.vdqg.entity.Award;
import com.vdqg.entity.AwardPayment;
import com.vdqg.entity.AwardResult;
import com.vdqg.entity.Match;
import com.vdqg.entity.MatchDetail;
import com.vdqg.entity.Season;
import com.vdqg.repository.AwardPaymentRepository;
import com.vdqg.repository.AwardRepository;
import com.vdqg.repository.AwardResultRepository;
import com.vdqg.repository.MatchRepository;
import com.vdqg.repository.SeasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AwardPaymentService {

    private static final String UNKNOWN_LABEL = "Chưa xác định";
    private static final String ACTIVE_AWARD_STATUS = "ĐANG ÁP DỤNG";
    private static final String PAID_STATUS = "ĐÃ THANH TOÁN";
    private static final String PAYMENT_SUCCESS = "THÀNH CÔNG";
    private static final String HOME_ROLE = "NHÀ";
    private static final String AWAY_ROLE = "KHÁCH";

    private final SeasonRepository seasonRepository;
    private final AwardRepository awardRepository;
    private final AwardResultRepository awardResultRepository;
    private final MatchRepository matchRepository;
    private final AwardPaymentRepository paymentRepository;

    public List<Season> getAllSeasons() {
        return seasonRepository.findAll();
    }

    public Season findSeasonById(Long seasonId) {
        return seasonRepository.findById(seasonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mùa giải!"));
    }

    public List<Award> getAwardsBySeason(Long seasonId) {
        findSeasonById(seasonId);
        return awardRepository.findBySeasonId(seasonId).stream()
                .filter(this::isActiveAward)
                .toList();
    }

    public List<Match> getMatchesBySeason(Long seasonId) {
        return matchRepository.findBySeasonIdOrderByMatchDateAsc(seasonId);
    }

    public Match findMatchById(Long matchId) {
        return matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trận đấu!"));
    }

    public Map<Long, String> getMatchDisplayNames(List<Match> matches) {
        Map<Long, String> result = new LinkedHashMap<>();
        for (Match match : matches) {
            if (match != null && match.getId() != null) {
                result.put(match.getId(), buildDisplayName(match));
            }
        }
        return result;
    }

    public String getMatchDisplayName(Long matchId) {
        return matchRepository.findById(matchId)
                .map(this::buildDisplayName)
                .orElse(UNKNOWN_LABEL);
    }

    public List<Award> getAwardsByMatch(Long matchId) {
        return awardRepository.findByMatchId(matchId).stream()
                .filter(this::isActiveAward)
                .toList();
    }

    public Award findAwardById(Long awardId) {
        return awardRepository.findById(awardId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giải thưởng!"));
    }

    public List<AwardResult> getResultsByAward(Long awardId) {
        return awardResultRepository.findByAwardId(awardId);
    }

    public AwardResult findResultById(Long id) {
        return awardResultRepository.findDetailedById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người nhận giải!"));
    }

    public AwardResult findPayableResultById(Long id) {
        AwardResult result = findResultById(id);
        ensurePayable(result);
        return result;
    }

    public String resolveSeasonName(AwardResult result) {
        if (result == null || result.getAward() == null) {
            return UNKNOWN_LABEL;
        }

        Award award = result.getAward();
        if (award.getSeason() != null && award.getSeason().getSeasonName() != null) {
            return award.getSeason().getSeasonName();
        }

        if (award.getMatch() != null && award.getMatch().getId() != null) {
            return seasonRepository.findByMatchId(award.getMatch().getId())
                    .map(Season::getSeasonName)
                    .filter(name -> name != null && !name.isBlank())
                    .orElse(UNKNOWN_LABEL);
        }

        return UNKNOWN_LABEL;
    }

    public Long resolveSeasonId(AwardResult result) {
        if (result == null || result.getAward() == null) {
            return null;
        }

        Award award = result.getAward();
        if (award.getSeason() != null) {
            return award.getSeason().getId();
        }

        if (award.getMatch() != null && award.getMatch().getId() != null) {
            return seasonRepository.findByMatchId(award.getMatch().getId())
                    .map(Season::getId)
                    .orElse(null);
        }

        return null;
    }

    @Transactional
    public AwardPayment processPayment(Long awardResultId, String paymentMethod, String note) {
        AwardResult result = findPayableResultById(awardResultId);

        AwardPayment payment = new AwardPayment();
        payment.setAwardResult(result);
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(result.getAward().getPrizeAmount());
        payment.setPaidAt(LocalDateTime.now());
        payment.setNote(note);
        payment.setTransactionStatus(PAYMENT_SUCCESS);
        paymentRepository.save(payment);

        result.setPaymentStatus(PAID_STATUS);
        awardResultRepository.save(result);

        return payment;
    }

    private void ensurePayable(AwardResult result) {
        if (isPaid(result.getPaymentStatus()) || paymentRepository.existsByAwardResultId(result.getId())) {
            throw new IllegalStateException("Giải thưởng này đã được thanh toán trước đó!");
        }
    }

    private boolean isActiveAward(Award award) {
        return sameText(award.getStatus(), ACTIVE_AWARD_STATUS);
    }

    private boolean isPaid(String paymentStatus) {
        return sameText(paymentStatus, PAID_STATUS);
    }

    private String buildDisplayName(Match match) {
        List<MatchDetail> details = match.getMatchDetails() != null ? match.getMatchDetails() : List.of();
        String homeTeam = resolveTeamName(details, HOME_ROLE, 0);
        String awayTeam = resolveTeamName(details, AWAY_ROLE, 1);
        return homeTeam + " vs " + awayTeam;
    }

    private String resolveTeamName(List<MatchDetail> details, String role, int fallbackIndex) {
        for (MatchDetail detail : details) {
            if (detail != null
                    && sameText(detail.getRole(), role)
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

    private boolean sameText(String actual, String expected) {
        return normalizeText(actual).equals(normalizeText(expected));
    }

    private String normalizeText(String value) {
        if (value == null) {
            return "";
        }
        if (looksMojibake(value)) {
            return new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        }
        return value;
    }

    private boolean looksMojibake(String value) {
        return value.contains("Ã")
                || value.contains("Ä")
                || value.contains("Æ")
                || value.contains("Â")
                || value.contains("áº")
                || value.contains("á»");
    }
}
