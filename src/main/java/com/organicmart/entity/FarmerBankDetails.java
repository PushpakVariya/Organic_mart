package com.organicmart.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "farmer_bank_details")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FarmerBankDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_profile_id", nullable = false, unique = true)
    private FarmerProfile farmerProfile;

    private String accountHolderName;
    private String accountNumber;
    private String ifscCode;
    private String bankName;
    private String upiId;
}
