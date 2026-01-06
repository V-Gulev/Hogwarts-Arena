package main.web;

import jakarta.servlet.http.HttpSession;
import main.exception.CustomException;
import main.exception.InvalidCredentials;
import main.service.SpellService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
public class SpellController {


    private final SpellService spellService;

    public SpellController(SpellService spellService) {
        this.spellService = spellService;
    }

    @PostMapping("/spells")
    public String learnSpell(@RequestParam("spell-code") String spellCode, HttpSession session, RedirectAttributes redirectAttributes) {

        UUID wizardId = (UUID) session.getAttribute("user_id");

        if (wizardId == null) {
            return "redirect:/login";
        }


        spellService.learnSpell(wizardId, spellCode);

        return "redirect:/home";
    }
}
