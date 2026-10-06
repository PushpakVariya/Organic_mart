package com.organicmart.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false, length = 30)
    private String paymentMethod; // COD, UPI, CARD

    @Builder.Default
    private String paymentStatus = "PENDING"; // PENDING, SUCCESS, FAILED, REFUNDED

    private String transactionRef;
    private LocalDateTime paidAt;
}
