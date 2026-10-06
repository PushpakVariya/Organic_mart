package com.organicmart.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String label; // HOME, WORK, OTHER
    private String recipientName;
    private String phone;

    @Column(nullable = false)
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pinCode;

    @Builder.Default
    private boolean isDefault = false;
}
