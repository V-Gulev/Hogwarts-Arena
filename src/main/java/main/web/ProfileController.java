package main.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import main.model.Wizard;
import main.repository.WizardRepository;
import main.service.WizardService;
import main.web.dto.EditWizardRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class ProfileController {


    private final WizardService wizardService;

    public ProfileController(WizardService wizardService) {
        this.wizardService = wizardService;
    }



    @GetMapping
    public ModelAndView viewProfile(HttpSession session) {
        UUID wizardId = (UUID) session.getAttribute("user_id");

        if (wizardId == null) {
            return new ModelAndView("redirect:/login");
        }

        ModelAndView modelAndView = new ModelAndView("profile");

        Wizard wizard = wizardService.findById(wizardId);

        modelAndView.addObject("wizard", wizard);

        EditWizardRequest dto = new EditWizardRequest();
        dto.setUsername(wizard.getUsername());
        dto.setAvatarUrl(wizard.getAvatarUrl());
        modelAndView.addObject("editWizardRequest", dto);


        return modelAndView;
    }


    @PutMapping
    public String updateProfile(@Valid @ModelAttribute("wizardProfileEditDTO") EditWizardRequest wizardProfileEditDTO, BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes) {

        UUID wizardId = (UUID) session.getAttribute("user_id");
        if (wizardId == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("wizardProfileEditDTO", wizardProfileEditDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.wizardProfileEditDTO", bindingResult);
            return "redirect:/profile";
        }

        wizardService.updateProfile(wizardId, wizardProfileEditDTO);
        return "redirect:/profile";
    }

    @PatchMapping("/alignment")
    public String changeAlignment(HttpSession session, RedirectAttributes redirectAttributes) {
        UUID wizardId = (UUID) session.getAttribute("user_id");
        if (wizardId == null) {
            return "redirect:/login";
        }

        wizardService.changeAlignmentToDark(wizardId);
        redirectAttributes.addFlashAttribute("success", "You have embraced the dark path.");
        return "redirect:/profile";
    }
}
