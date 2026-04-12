// entity/Round.java
package com.vdqg.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "rounds")
@Data @NoArgsConstructor @AllArgsConstructor
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @Column(name = "round_name", nullable = false)
    private String roundName;            // VD: "Vòng 1", "Tứ kết"

    @Column(name = "round_order")
    private Integer roundOrder;          // Thứ tự vòng

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL)
    private List<Match> matches;
}