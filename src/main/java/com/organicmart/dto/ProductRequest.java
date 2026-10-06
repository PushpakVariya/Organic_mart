package com.organicmart.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ProductRequest {

    @NotBlank
    private String name;

    @NotNull
    private Long categoryId;

    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    private String weight;

    @NotNull
    @Min(0)
    private Integer stock;

    private LocalDate harvestDate;
    private LocalDate expiryDate;
}
