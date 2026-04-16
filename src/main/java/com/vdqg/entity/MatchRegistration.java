package com.vdqg.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "match_registrations", uniqueConstraints = @UniqueConstraint(columnNames = {"match_detail_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_detail_id", nullable = false, unique = true)
    private MatchDetail matchDetail;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "match_registration_id", nullable = false)
    @OrderBy("id ASC")
    private List<RegistrationDetail> registrationDetails = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (registeredAt == null) {
            registeredAt = LocalDateTime.now();
        }
    }

    public void addRegistrationDetail(RegistrationDetail registrationDetail) {
        registrationDetails.add(registrationDetail);
    }
}
