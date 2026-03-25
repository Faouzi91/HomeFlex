package com.realestate.rental.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LanguageUpdateRequest(
        @NotBlank(message = "Language is required") @Pattern(regexp = "^(en|fr|ar|es)$", message = "Language must be en, fr, ar, or es") String language
) {}
