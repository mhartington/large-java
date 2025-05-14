package com.enterprise.user.entity;

import com.enterprise.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile extends BaseEntity {

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String middleName;

    private String phoneNumber;

    private LocalDate dateOfBirth;

    @Column(length = 2000)
    private String bio;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String profilePictureUrl;

    @Embedded
    private Address address;

    @Embedded
    private EmergencyContact emergencyContact;

    public enum Gender {
        MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY
    }
}