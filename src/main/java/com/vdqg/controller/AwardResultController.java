package com.vdqg.controller;

import com.vdqg.entity.Award;
import com.vdqg.entity.AwardResult;
import com.vdqg.service.AwardResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/award-results")
@RequiredArgsConstructor
public class AwardResultController {

    private final AwardResultService awardResultService;

    @GetMapping("/award/{awardId}")
    public String listByAward(@PathVariable Long awardId, Model model) {
        Award award = awardResultService.findAwardById(awardId);
        model.addAttribute("award", award);
        model.addAttribute("results", awardResultService.getResultsByAward(awardId));
        return "award/result-list";
    }

    @GetMapping("/award/{awardId}/new")
    public String showCreateForm(@PathVariable Long awardId, Model model) {
        Award award = awardResultService.findAwardById(awardId);
        model.addAttribute("award", award);
        model.addAttribute("awardResult", new AwardResult());
        populateOptions(model, award);
        model.addAttribute("isEdit", false);
        return "award/result-form";
    }

    @GetMapping("/{resultId}/edit")
    public String showEditForm(@PathVariable Long resultId,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            AwardResult result = awardResultService.findEditableResultById(resultId);
            Award award = result.getAward();
            model.addAttribute("award", award);
            model.addAttribute("awardResult", result);
            populateOptions(model, award);
            model.addAttribute("isEdit", true);
            return "award/result-form";
        } catch (IllegalStateException e) {
            AwardResult result = awardResultService.findResultById(resultId);
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/award-results/award/" + result.getAward().getId();
        }
    }

    @PostMapping("/award/{awardId}/save")
    public String save(@PathVariable Long awardId,
                       @ModelAttribute AwardResult awardResult,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        try {
            boolean isCreate = awardResult.getId() == null;
            awardResultService.save(awardId, awardResult);
            redirectAttributes.addFlashAttribute(
                    "successMsg",
                    isCreate ? "ÄÃ£ thÃªm ngÆ°á»i nháº­n giáº£i!" : "ÄÃ£ cáº­p nháº­t ngÆ°á»i nháº­n giáº£i!");
            return "redirect:/award-results/award/" + awardId;
        } catch (IllegalArgumentException | IllegalStateException e) {
            Award award = awardResultService.findAwardById(awardId);
            model.addAttribute("award", award);
            model.addAttribute("awardResult", awardResult);
            populateOptions(model, award);
            model.addAttribute("isEdit", awardResult.getId() != null);
            model.addAttribute("errorMsg", e.getMessage());
            return "award/result-form";
        }
    }

    @PostMapping("/{resultId}/delete")
    public String delete(@PathVariable Long resultId, RedirectAttributes redirectAttributes) {
        AwardResult result = awardResultService.findResultById(resultId);
        Long awardId = result.getAward().getId();

        try {
            awardResultService.delete(resultId);
            redirectAttributes.addFlashAttribute("successMsg", "ÄÃ£ xÃ³a ngÆ°á»i nháº­n giáº£i!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }

        return "redirect:/award-results/award/" + awardId;
    }

    private void populateOptions(Model model, Award award) {
        model.addAttribute("teamOptions", awardResultService.getTeamOptions(award));
        model.addAttribute("playerOptions", awardResultService.getPlayerOptions(award));
    }
}
