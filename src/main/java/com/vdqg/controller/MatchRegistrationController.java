package com.vdqg.controller;

import com.vdqg.entity.Match;
import com.vdqg.entity.MatchDetail;
import com.vdqg.service.MatchRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class MatchRegistrationController {

    private final MatchRegistrationService regService;

    @GetMapping
    public String selectMatch(
            @RequestParam(required = false) Long seasonId,
            @RequestParam(required = false) Long roundId,
            Model model) {

        model.addAttribute("seasons", regService.getActiveSeasons());

        if (seasonId != null) {
            model.addAttribute("selectedSeasonId", seasonId);
            model.addAttribute("rounds", regService.getRoundsBySeason(seasonId));
        }
        if (roundId != null) {
            model.addAttribute("selectedRoundId", roundId);
            model.addAttribute("matches", regService.getMatchesByRound(roundId));
        }
        return "registration/select";
    }

    @GetMapping("/match-detail/{matchDetailId}")
    public String showForm(@PathVariable Long matchDetailId, Model model) {
        MatchDetail matchDetail = regService.findMatchDetailById(matchDetailId);
        Match match = matchDetail.getMatch();

        model.addAttribute("match", match);
        model.addAttribute("selectedMatchDetail", matchDetail);
        model.addAttribute("matchDetails", regService.getMatchDetails(match.getId()));
        model.addAttribute("contracts", regService.getPlayerContractsForMatchDetail(matchDetailId));
        return "registration/form";
    }

    @PostMapping("/match-detail/{matchDetailId}/confirm")
    public String confirm(
            @PathVariable Long matchDetailId,
            @RequestParam List<Long> startingContractIds,
            @RequestParam(required = false) List<Long> substituteContractIds,
            RedirectAttributes ra) {
        try {
            regService.registerPlayers(matchDetailId, startingContractIds, substituteContractIds);
            ra.addFlashAttribute("successMsg", "Đăng ký đội hình thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/registration/match-detail/" + matchDetailId;
        }
        return "redirect:/registration";
    }
}
