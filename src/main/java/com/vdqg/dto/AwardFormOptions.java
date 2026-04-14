package com.vdqg.dto;

import com.vdqg.entity.Match;
import com.vdqg.entity.Season;
import java.util.List;
import java.util.Map;

public record AwardFormOptions(
    List<Season> seasonOptions,
    List<Match> matches,
    Map<Long, String> matchDisplayNames
) {}
