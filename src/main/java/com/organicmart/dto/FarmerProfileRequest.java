package com.organicmart.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FarmerProfileRequest {

    @NotBlank
    private String farmName;
    @NotBlank
    private String village;
    private String taluka;
    @NotBlank
    private String district;
    @NotBlank
    private String state;
    @NotBlank
    private String pinCode;

    private String accountHolderName;
    private String accountNumber;
    private String ifscCode;
    private String bankName;
    private String upiId;
}
