package com.realestate.rental.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LanguageUpdateRequest {
    @NotBlank(message = "Language is required")
    @Pattern(regexp = "^(en|fr)$", message = "Language must be 'en' or 'fr'")
    private String language;
}