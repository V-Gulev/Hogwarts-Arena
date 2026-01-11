package main.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import main.model.WizardAlignment;
import org.hibernate.validator.constraints.URL;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArenaWizardDto {

    @NotBlank
    @Size(min = 6, max = 12)
    private String username;

    @NotBlank
    @URL
    private String avatarUrl;

    @NotBlank
    private WizardAlignment wizardAlignment;

    @PositiveOrZero
    private int spellCount;

    @PositiveOrZero
    private long totalPower;
}
