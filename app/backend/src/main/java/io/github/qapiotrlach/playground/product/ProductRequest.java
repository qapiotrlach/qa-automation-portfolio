package io.github.qapiotrlach.playground.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank(message = "Nazwa jest wymagana")
        @Size(max = 120, message = "Nazwa moze miec maksymalnie 120 znakow")
        String name,

        @Size(max = 2000, message = "Opis moze miec maksymalnie 2000 znakow")
        String description,

        @NotNull(message = "Cena jest wymagana")
        @DecimalMin(value = "0.00", message = "Cena nie moze byc ujemna")
        @Digits(integer = 8, fraction = 2, message = "Cena musi miec maksymalnie 2 miejsca po przecinku")
        BigDecimal price,

        @NotNull(message = "Stan magazynowy jest wymagany")
        @Min(value = 0, message = "Stan magazynowy nie moze byc ujemny")
        Integer stock,

        @NotBlank(message = "Kategoria jest wymagana")
        @Size(max = 60, message = "Kategoria moze miec maksymalnie 60 znakow")
        String category,

        Boolean active
) {}
