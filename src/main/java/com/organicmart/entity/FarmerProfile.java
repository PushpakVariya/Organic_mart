package com.organicmart.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "farmer_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FarmerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String farmName;
    private String village;
    private String taluka;
    private String district;
    private String state;
    private String pinCode;
    private String aadhaarDocumentUrl;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
