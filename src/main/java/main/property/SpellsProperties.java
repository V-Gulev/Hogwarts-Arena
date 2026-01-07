package main.property;

import lombok.Data;
import main.config.YamlPropertySourceFactory;
import main.model.SpellAlignment;
import main.model.SpellCategory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties
@PropertySource(value = "spells.yaml", factory = YamlPropertySourceFactory.class)
public class SpellsProperties {

    private List<SpellDetails> spells;

    @Data
    public static class SpellDetails {
        private String code;
        private String name;
        private String image;
        private SpellCategory category;
        private SpellAlignment alignment;
        private int minLearned;
        private int power;
        private String description;
    }

    public List<SpellDetails> getSpellDetailsByMinLearned(int minLearned) {
        return spells.stream()
                .filter(spell -> spell.getMinLearned() == minLearned).toList();
    }




}
