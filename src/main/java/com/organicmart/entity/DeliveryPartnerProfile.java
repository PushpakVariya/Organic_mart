package com.organicmart.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "delivery_partner_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryPartnerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String vehicleNumber;
    private String coverageArea;

    @Builder.Default
    private String approvalStatus = "PENDING"; // PENDING, APPROVED, REJECTED
}
