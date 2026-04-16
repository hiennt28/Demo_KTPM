package com.vdqg.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "registration_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDetail {

    public static final String STARTING_ROLE = "ĐÁ CHÍNH";
    public static final String SUBSTITUTE_ROLE = "DỰ BỊ";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role", nullable = false, length = 30)
    private String role;

    @Column(name = "playing_position")
    private String playingPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_contract_id", nullable = false)
    private PlayerContract playerContract;
}
