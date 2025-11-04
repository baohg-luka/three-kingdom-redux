package com.example.three_kingdom_backend.match;

import com.example.three_kingdom_backend.match.enums.EnumKingdom;
import com.example.three_kingdom_backend.util.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "kingdom_info")
@Data
@EqualsAndHashCode(callSuper = false)
public class KingdomInfo extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EnumKingdom kingdom;

    private Integer gold;

    private Integer rice;

    private Integer populationSupportToken;

    @Column(name = "untrained_troops")
    private Integer unTrainedTroops;

    private Integer trainedTroops;

    private Integer spear;

    private Integer crossbow;

    private Integer horse;

    private Integer vessel;

    private Integer redCard;

    private Integer yellowCard;

    private Integer totalGeneral;

    private Integer stationGeneral;

    private Integer unusedGeneral;

    @OneToMany(mappedBy = "kingdomInfo", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Building> buildings = new ArrayList<>();

    private Integer flippedMarket;

    private Integer flippedFarm;

    private Integer developedMarket;

    private Integer developedFarm;

    @Column(name = "market_flag_vp")
    private Integer marketFlagVP;

    @Column(name = "farm_flag_vp")
    private Integer farmFlagVP;

    @Column(name = "market_flag_no_vp")
    private Integer marketFlagNoVP;

    @Column(name = "farm_flag_no_vp")
    private Integer farmFlagNoVP;

    private Integer militaryVictoryPoints;

    private Integer economicLevel;

    private Integer tribalLevel;

    private Integer rankLevel;

    private Integer wuBorderLevel;

    private Integer shuBorderLevel;

    private Integer weiBorderLevel;

    private Integer stationTroops;

    private Boolean isEmperorToken;

}
