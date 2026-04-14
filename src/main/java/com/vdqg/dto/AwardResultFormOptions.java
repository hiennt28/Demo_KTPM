package com.vdqg.dto;

import com.vdqg.entity.Player;
import com.vdqg.entity.Team;
import java.util.List;

public record AwardResultFormOptions(
    List<Team> teamOptions,
    List<Player> playerOptions
) {}
