package com.example.three_kingdom_backend.match;

import com.example.three_kingdom_backend.match.enums.EnumBuildingType;
import com.example.three_kingdom_backend.util.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ForeignKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "buildings")
@Data
@EqualsAndHashCode(callSuper = false)
public class Building extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "kingdom_info_id", nullable = false, foreignKey = @ForeignKey(name = "fk_building_kingdom_info"))
    private KingdomInfo kingdomInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EnumBuildingType type;

    private String name;

    private String description;

    private String ability;

    private Integer vp;

    private String cost;
    // Plan: cost store like
    // [gold/rice/populationSupportToken/troops/spear/crossbow/horse/vessel]
    // Example: cost of 2 train/untrain + 1 gold will store as "10020000"

}
