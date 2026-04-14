package com.vdqg.controller;

import com.vdqg.dto.AwardFormOptions;
import com.vdqg.entity.Award;
import com.vdqg.entity.Match;
import com.vdqg.service.AwardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/awards")
@RequiredArgsConstructor
public class AwardController {

    private final AwardService awardService;

    @GetMapping
    public String list(Model model) {
        List<Award> awards = awardService.getAllActiveAwards();
        model.addAttribute("awards", awards);
        model.addAttribute("matchDisplayNames", awardService.getDisplayNamesForAwards(awards));
        return "award/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("award", new Award());
        populateMatchOptions(model);
        model.addAttribute("isEdit", false);
        return "award/form";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("award", awardService.findById(id));
        populateMatchOptions(model);
        model.addAttribute("isEdit", true);
        return "award/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Award award,
                       BindingResult result,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            populateMatchOptions(model);
            model.addAttribute("isEdit", award.getId() != null);
            return "award/form";
        }
        try {
            awardService.save(award);
            redirectAttributes.addFlashAttribute("successMsg",
                    award.getId() == null ? "Thêm giải thưởng thành công!" : "Cập nhật thành công!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMsg", e.getMessage());
            populateMatchOptions(model);
            model.addAttribute("isEdit", award.getId() != null);
            return "award/form";
        }
        return "redirect:/awards";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        awardService.softDelete(id);
        redirectAttributes.addFlashAttribute("successMsg", "Đã hủy áp dụng giải thưởng!");
        return "redirect:/awards";
    }

    private void populateMatchOptions(Model model) {
        AwardFormOptions options = awardService.getFormOptions();
        model.addAttribute("seasonOptions", options.seasonOptions());
        model.addAttribute("matches", options.matches());
        model.addAttribute("matchDisplayNames", options.matchDisplayNames());
    }
}
