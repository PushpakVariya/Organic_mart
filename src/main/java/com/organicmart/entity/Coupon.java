package com.organicmart.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "coupons")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    private BigDecimal discountPercent;
    private BigDecimal flatDiscount;

    @Builder.Default
    private BigDecimal minOrderAmount = BigDecimal.ZERO;

    private LocalDate validFrom;
    private LocalDate validTo;

    @Builder.Default
    private boolean active = true;
}
