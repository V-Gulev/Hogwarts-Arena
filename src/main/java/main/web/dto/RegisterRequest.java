package main.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import main.model.House;
import main.model.WizardAlignment;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.NumberFormat;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Size(min = 6, max = 12)
    private String username;

    @NotBlank
    @Size(min = 6, max = 6)
    private String password;

    @NotBlank
    @URL
    private String avatarUrl;

    @NotNull
    private House house;

    @NotNull
    private WizardAlignment alignment;
}
