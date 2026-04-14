
package com.vdqg.util;

import com.vdqg.entity.MatchDetail;
import com.vdqg.repository.MatchDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MatchDisplayNameUtil {

    private static final String UNKNOWN_TEAM = "Chưa xác định";
    private final MatchDetailRepository matchDetailRepository;

    public Map<Long, String> buildDisplayNames(List<Long> matchIds) {
        if (matchIds.isEmpty()) return new LinkedHashMap<>();

        Map<Long, List<MatchDetail>> detailsByMatchId = matchDetailRepository
            .findByMatchIdIn(matchIds).stream()
            .collect(Collectors.groupingBy(
                d -> d.getMatch().getId(),
                LinkedHashMap::new,
                Collectors.toCollection(ArrayList::new)
            ));

        Map<Long, String> result = new LinkedHashMap<>();
        for (Long id : matchIds) {
            List<MatchDetail> details = detailsByMatchId.getOrDefault(id, List.of());
            String home = getTeamNameByRole(details, "NHÀ", 0);
            String away = getTeamNameByRole(details, "KHÁCH", 1);
            result.put(id, home + " vs " + away);
        }
        return result;
    }

    private String getTeamNameByRole(List<MatchDetail> details, String role, int fallback) {
        for (MatchDetail d : details) {
            if (d != null && Objects.equals(role, d.getRole())
                    && d.getTeam() != null && d.getTeam().getTeamName() != null) {
                return d.getTeam().getTeamName();
            }
        }
        if (fallback >= 0 && fallback < details.size()) {
            MatchDetail d = details.get(fallback);
            if (d != null && d.getTeam() != null) return d.getTeam().getTeamName();
        }
        return UNKNOWN_TEAM;
    }
}