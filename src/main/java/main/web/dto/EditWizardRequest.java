package main.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditWizardRequest {

    @NotBlank
    @Size(min = 6, max = 12)
    private String username;

    @NotBlank
    @URL
    private String avatarUrl;

}
