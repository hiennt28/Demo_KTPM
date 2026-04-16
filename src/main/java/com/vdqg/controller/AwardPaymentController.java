package com.vdqg.controller;

import com.vdqg.entity.AwardPayment;
import com.vdqg.entity.AwardResult;
import com.vdqg.entity.Match;
import com.vdqg.entity.Season;
import com.vdqg.service.AwardPaymentService;
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
@RequestMapping("/payment")
@RequiredArgsConstructor
public class AwardPaymentController {

    private final AwardPaymentService paymentService;

    @GetMapping
    public String selectSeason(Model model) {
        model.addAttribute("seasons", paymentService.getAllSeasons());
        return "payment/list";
    }

    @GetMapping("/season/{seasonId}")
    public String listSeasonData(@PathVariable Long seasonId, Model model) {
        Season season = paymentService.findSeasonById(seasonId);
        List<Match> matches = paymentService.getMatchesBySeason(seasonId);

        model.addAttribute("seasons", paymentService.getAllSeasons());
        model.addAttribute("selectedSeason", season);
        model.addAttribute("seasonAwards", paymentService.getAwardsBySeason(seasonId));
        model.addAttribute("matches", matches);
        model.addAttribute("matchDisplayNames", paymentService.getMatchDisplayNames(matches));
        return "payment/list";
    }

    @GetMapping("/season/{seasonId}/match/{matchId}")
    public String listMatchAwards(@PathVariable Long seasonId,
                                  @PathVariable Long matchId,
                                  Model model) {
        Season season = paymentService.findSeasonById(seasonId);
        List<Match> matches = paymentService.getMatchesBySeason(seasonId);

        model.addAttribute("seasons", paymentService.getAllSeasons());
        model.addAttribute("selectedSeason", season);
        model.addAttribute("seasonAwards", paymentService.getAwardsBySeason(seasonId));
        model.addAttribute("matches", matches);
        model.addAttribute("matchDisplayNames", paymentService.getMatchDisplayNames(matches));
        model.addAttribute("selectedMatchId", matchId);
        model.addAttribute("matchAwards", paymentService.getAwardsByMatch(matchId));
        return "payment/list";
    }

    @GetMapping("/match/{matchId}/modal")
    public String loadMatchAwardsModal(@PathVariable Long matchId, Model model) {
        model.addAttribute("fragmentOnly", true);
        model.addAttribute("matchName", paymentService.getMatchDisplayName(matchId));
        model.addAttribute("awards", paymentService.getAwardsByMatch(matchId));
        return "payment/list :: matchAwardsContent";
    }

    @GetMapping("/award/{awardId}/modal")
    public String loadResultsModal(@PathVariable Long awardId, Model model) {
        model.addAttribute("fragmentOnly", true);
        model.addAttribute("award", paymentService.findAwardById(awardId));
        model.addAttribute("results", paymentService.getResultsByAward(awardId));
        return "payment/list :: resultsContent";
    }

    @GetMapping("/result/{resultId}/pay")
    public String showPayForm(@PathVariable Long resultId,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            AwardResult result = paymentService.findPayableResultById(resultId);
            model.addAttribute("result", result);
            model.addAttribute("seasonName", paymentService.resolveSeasonName(result));
            model.addAttribute("selectedSeasonId", paymentService.resolveSeasonId(result));
            return "payment/payment";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/payment";
        }
    }

    @PostMapping("/result/{resultId}/pay")
    public String processPayment(@PathVariable Long resultId,
                                 @RequestParam String paymentMethod,
                                 @RequestParam(required = false) String note,
                                 Model model,
                                 RedirectAttributes ra) {
        try {
            AwardPayment payment = paymentService.processPayment(resultId, paymentMethod, note);
            model.addAttribute("payment", payment);
            model.addAttribute("result", payment.getAwardResult());
            model.addAttribute("seasonName", paymentService.resolveSeasonName(payment.getAwardResult()));
            model.addAttribute("selectedSeasonId", paymentService.resolveSeasonId(payment.getAwardResult()));
            return "payment/receipt";
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/payment";
        }
    }
}
