package main.service;

import jakarta.transaction.Transactional;
import main.exception.CustomException;
import main.exception.InvalidCredentials;
import main.model.Spell;
import main.model.SpellAlignment;
import main.model.Wizard;
import main.model.WizardAlignment;
import main.property.SpellsProperties;
import main.property.SpellsProperties.SpellDetails;
import main.repository.SpellRepository;
import main.repository.WizardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SpellService {

    private final SpellsProperties spellsProperties;
    private final SpellRepository spellRepository;
    private final WizardRepository wizardRepository;


    public SpellService(SpellRepository spellRepository, SpellsProperties spellsProperties, WizardRepository wizardRepository) {
        this.spellRepository = spellRepository;
        this.spellsProperties = spellsProperties;
        this.wizardRepository = wizardRepository;
    }

    public Spell createNewSpell(SpellDetails chosenSpell) {

        Spell spell = Spell.builder()
                .code(chosenSpell.getCode())
                .name(chosenSpell.getName())
                .image(chosenSpell.getImage())
                .category(chosenSpell.getCategory())
                .alignment(chosenSpell.getAlignment())
                .power(chosenSpell.getPower())
                .description(chosenSpell.getDescription())
                .createdOn(LocalDateTime.now())
                .build();


        return spell;
    }

    public Spell save(Spell spell) {
        return spellRepository.save(spell);
    }


    public List<SpellDetails> getUnlearnedSpells(Wizard wizard) {
        Set<String> learnedCodes = wizard.getSpells()
                .stream()
                .map(Spell::getCode)
                .collect(Collectors.toSet());

        return spellsProperties.getSpells()
                .stream()
                .filter(spell -> !learnedCodes.contains(spell.getCode()))
                .toList();
    }


    public List<SpellDetails> getLockedSpells(Wizard wizard) {
        List<SpellDetails> unlearned = getUnlearnedSpells(wizard);
        return unlearned.stream()
                .filter(s -> s.getMinLearned() > wizard.getSpells().size())
                .toList();
    }

    public List<SpellDetails> getAvailableSpells(Wizard wizard) {
        List<SpellDetails> unlearned = getUnlearnedSpells(wizard);
        return unlearned.stream()
                .filter(s -> s.getMinLearned() <= wizard.getSpells()
                        .size()).toList();
    }


    @Transactional
    public void learnSpell(UUID wizardId, String spellCode) {

        Wizard wizard = wizardRepository.findById(wizardId).orElseThrow(() -> new IllegalArgumentException("Invalid User!"));

        SpellDetails spellToBeLearned = spellsProperties.getSpells().stream()
                .filter(spell -> spell.getCode().equals(spellCode))
                .findFirst()
                .orElseThrow(() -> new InvalidCredentials("Invalid spell code: " + spellCode));


        if (wizard.getSpells().size() < spellToBeLearned.getMinLearned()) {
            throw new IllegalStateException("You have not learned enough spells to learn " + spellToBeLearned.getName() + ".");
        }

        boolean alreadyLearned = wizard.getSpells().stream().anyMatch(spell -> spell.getCode().equals(spellCode));

        if (alreadyLearned) {
            throw new IllegalStateException("You have already learned " + spellToBeLearned.getName() + ".");
        }

        if (wizard.getAlignment() == WizardAlignment.LIGHT && spellToBeLearned.getAlignment() == SpellAlignment.DARK) {
            wizard.setAlignment(WizardAlignment.DARK);
        }

        Spell newSpell = createNewSpell(spellToBeLearned);
        newSpell.setWizard(wizard);
        spellRepository.save(newSpell);
        wizardRepository.save(wizard);
    }
}
