package main.web;

import jakarta.servlet.http.HttpSession;
import main.model.House;
import main.service.WizardService;
import main.web.dto.ArenaWizardDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class ArenaController {

    private final WizardService wizardService;

    public ArenaController(WizardService wizardService) {
        this.wizardService = wizardService;
    }


    @GetMapping("/arena")
    public ModelAndView viewArena(HttpSession session) {
        UUID wizardId = (UUID) session.getAttribute("user_id");
        if (wizardId == null) {
            return new ModelAndView("redirect:/login");
        }


        ModelAndView modelAndView = new ModelAndView("arena");


        modelAndView.addObject("gryffindorWizards", wizardService.findGryffindorWizards());
        modelAndView.addObject("slytherinWizards", wizardService.findSlytherinWizards());
        modelAndView.addObject("ravenclawWizards", wizardService.findRavenclawWizards());
        modelAndView.addObject("hufflepuffWizards", wizardService.findHufflepuffWizards());

        return modelAndView;
    }
}
