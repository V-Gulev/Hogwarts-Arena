package main.service;

import jakarta.transaction.Transactional;
import main.exception.InvalidCredentials;
import main.model.House;
import main.model.Spell;
import main.model.Wizard;
import main.model.WizardAlignment;
import main.property.SpellsProperties;
import main.property.SpellsProperties.SpellDetails;
import main.repository.WizardRepository;
import main.web.dto.ArenaWizardDto;
import main.web.dto.EditWizardRequest;
import main.web.dto.LoginRequest;
import main.web.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class WizardService {


    private final SpellsProperties spellsProperties;
    private final PasswordEncoder passwordEncoder;
    private final WizardRepository wizardRepository;
    private final SpellService spellService;

    public WizardService(PasswordEncoder passwordEncoder, WizardRepository wizardRepository, SpellsProperties spellsProperties, SpellService spellService) {
        this.passwordEncoder = passwordEncoder;
        this.wizardRepository = wizardRepository;
        this.spellsProperties = spellsProperties;
        this.spellService = spellService;
    }

    public void register(RegisterRequest registerRequest) {
        Wizard wizard = Wizard.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .avatarUrl(registerRequest.getAvatarUrl())
                .alignment(registerRequest.getAlignment())
                .house(registerRequest.getHouse())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();


        wizardRepository.save(wizard);
        assignRandomStartingSpell(wizard);
        update(wizard);

    }

    private void assignRandomStartingSpell(Wizard wizard) {
        List<SpellDetails> startingSpells = spellsProperties.getSpellDetailsByMinLearned(0);
        if (startingSpells.isEmpty()) {
            return;
        }

        int randomIndex = ThreadLocalRandom.current().nextInt(0, startingSpells.size());

        SpellDetails chosenSpell = startingSpells.get(randomIndex);

        Spell spell = spellService.createNewSpell(chosenSpell);

        spell.setWizard(wizard);

        spellService.save(spell);

        if (wizard.getSpells() == null) {
            wizard.setSpells(new ArrayList<>());
        }
        wizard.getSpells().add(spell);
    }

    public Wizard login(LoginRequest loginRequest) {

        Wizard wizard = wizardRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new InvalidCredentials("Username or Password incorrect!"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), wizard.getPassword())) {
            throw new InvalidCredentials("Username or Password incorrect!");
        }

        return wizard;
    }

    public Wizard findById(UUID userId) {
        return wizardRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentials("Invalid user id!"));
    }

    public void update(Wizard wizard) {
        wizardRepository.save(wizard);
    }

    @Transactional
    public void updateProfile(UUID wizardId, EditWizardRequest editDTO) {
        Wizard wizard = wizardRepository.findById(wizardId)
                .orElseThrow(() -> new IllegalStateException("Wizard not found."));

        wizard.setUsername(editDTO.getUsername());
        wizard.setAvatarUrl(editDTO.getAvatarUrl());

        wizardRepository.save(wizard);
    }

    @Transactional
    public void changeAlignmentToDark(UUID wizardId) {
        Wizard wizard = wizardRepository.findById(wizardId)
                .orElseThrow(() -> new IllegalStateException("Wizard not found with ID: " + wizardId));

        if (wizard.getAlignment() == WizardAlignment.LIGHT) {
            wizard.setAlignment(WizardAlignment.DARK);
            wizardRepository.save(wizard);
        }
    }

    public List<ArenaWizardDto> findGryffindorWizards() {
        List<Wizard> wizardByHouse = wizardRepository.findWizardByHouse(House.GRYFFINDOR);
        return sortWizardsForArena(wizardByHouse);
    }

    public List<ArenaWizardDto> findSlytherinWizards() {
        List<Wizard> wizardByHouse = wizardRepository.findWizardByHouse(House.SLYTHERIN);
        return sortWizardsForArena(wizardByHouse);
    }

    public List<ArenaWizardDto> findRavenclawWizards() {
        List<Wizard> wizardByHouse = wizardRepository.findWizardByHouse(House.RAVENCLAW);
        return sortWizardsForArena(wizardByHouse);
    }

    public List<ArenaWizardDto> findHufflepuffWizards() {
        List<Wizard> wizardByHouse = wizardRepository.findWizardByHouse(House.HUFFLEPUFF);
        return sortWizardsForArena(wizardByHouse);
    }

    public List<ArenaWizardDto> sortWizardsForArena(List<Wizard> wizards) {
        Comparator<WizardWithPower> comparator = Comparator.comparingLong(WizardWithPower::totalPower).reversed()
                .thenComparing(w -> w.wizard().getUsername());


        return wizards.stream().map(wizard -> new WizardWithPower(
                        wizard,
                        wizard.getSpells().stream().mapToLong(Spell::getPower).sum())).sorted(comparator)
                        .map(wp -> new ArenaWizardDto(
                        wp.wizard().getUsername(),
                        wp.wizard().getAvatarUrl(),
                        wp.wizard().getAlignment(),
                        wp.wizard().getSpells().size(),
                        wp.totalPower())).toList();
    }

    private record WizardWithPower(Wizard wizard, long totalPower) {
    }
}

