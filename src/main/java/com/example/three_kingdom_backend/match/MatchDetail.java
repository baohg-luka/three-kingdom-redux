package com.example.three_kingdom_backend.match;

import com.example.three_kingdom_backend.match.enums.EnumAllianceMarker;
import com.example.three_kingdom_backend.match.enums.EnumCriteria;
import com.example.three_kingdom_backend.match.enums.EnumKingdom;
import com.example.three_kingdom_backend.match.enums.EnumPhase;
import com.example.three_kingdom_backend.util.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "match_details")
@Data
@EqualsAndHashCode(callSuper = false)
public class MatchDetail extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false, foreignKey = @ForeignKey(name = "fk_match_detail_match"))
    private Match match;

    @Column(name = "round_number")
    private Integer roundNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "king_marker", length = 10)
    private EnumCriteria kingMarker;

    @Enumerated(EnumType.STRING)
    @Column(name = "population_marker", length = 10)
    private EnumCriteria populationMarker;

    @Enumerated(EnumType.STRING)
    @Column(name = "phase", length = 20)
    private EnumPhase phase;

    @Enumerated(EnumType.STRING)
    @Column(name = "alliance_marker", length = 20)
    private EnumAllianceMarker allianceMarker;

    @Enumerated(EnumType.STRING)
    @Column(name = "first_kingdom", length = 10)
    private EnumKingdom firstKingdom;

    @Enumerated(EnumType.STRING)
    @Column(name = "second_kingdom", length = 10)
    private EnumKingdom secondKingdom;

    @Enumerated(EnumType.STRING)
    @Column(name = "third_kingdom", length = 10)
    private EnumKingdom thirdKingdom;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wei_kingdom_info_id", foreignKey = @ForeignKey(name = "fk_match_detail_wei_kingdom"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private KingdomInfo weiKingdomInfo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shu_kingdom_info_id", foreignKey = @ForeignKey(name = "fk_match_detail_shu_kingdom"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private KingdomInfo shuKingdomInfo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wu_kingdom_info_id", foreignKey = @ForeignKey(name = "fk_match_detail_wu_kingdom"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private KingdomInfo wuKingdomInfo;

}
