package main.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import main.model.Spell;
import main.model.Wizard;
import main.property.SpellsProperties;
import main.property.SpellsProperties.SpellDetails;
import main.service.SpellService;
import main.service.WizardService;
import main.web.dto.LoginRequest;
import main.web.dto.RegisterRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
public class IndexController {

    private final SpellsProperties spellsProperties;
    private final WizardService wizardService;
    private final SpellService spellService;

    public IndexController(WizardService wizardService, SpellsProperties spellsProperties, SpellService spellService) {
        this.wizardService = wizardService;
        this.spellsProperties = spellsProperties;
        this.spellService = spellService;
    }

    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {
        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());

        return modelAndView;
    }

    @PostMapping("/register")
    public String register(@Valid RegisterRequest registerRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        wizardService.register(registerRequest);

        return "redirect:/login";
    }


    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("loginRequest", new LoginRequest());
        return modelAndView;
    }

    @PostMapping("/login")
    public String login(@Valid LoginRequest loginRequest, HttpSession session) {

        Wizard wizard = wizardService.login(loginRequest);
        session.setAttribute("user_id", wizard.getId());

        return "redirect:/home";
    }

    @GetMapping("/home")
    public ModelAndView getHomePage(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("home");

        UUID userId = (UUID) session.getAttribute("user_id");
        Wizard wizard = wizardService.findById(userId);
        List<SpellDetails> lockedSpells = spellService.getLockedSpells(wizard);
        List<SpellDetails> availableSpells = spellService.getAvailableSpells(wizard);



        modelAndView.addObject("wizard", wizard);
        modelAndView.addObject("lockedSpells", lockedSpells);
        modelAndView.addObject("availableSpells", availableSpells);

        return modelAndView;
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }



}
