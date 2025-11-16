package com.example.three_kingdom_backend.match.engine;

import com.example.three_kingdom_backend.match.Match;
import com.example.three_kingdom_backend.match.MatchDetail;
import com.example.three_kingdom_backend.match.KingdomInfo;
import com.example.three_kingdom_backend.match.enums.EnumKingdom;
import com.example.three_kingdom_backend.match.events.DomainEvent;
import com.example.three_kingdom_backend.match.events.GoldSpent;
import com.example.three_kingdom_backend.match.events.TroopsRaised;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Reducer: cập nhật state in-memory từ các DomainEvent. KHÔNG gọi DB ở đây. */
public final class MatchAggregate {

    private final Match header;
    private final MatchDetail detail;
    private final Map<EnumKingdom, KingdomInfo> kingdoms;

    public MatchAggregate(Match header, MatchDetail detail) {
        this.header = Objects.requireNonNull(header, "header");
        this.detail = Objects.requireNonNull(detail, "detail");
        this.kingdoms = new EnumMap<>(EnumKingdom.class);
        if (detail.getWeiKingdomInfo() != null)
            kingdoms.put(EnumKingdom.WEI, detail.getWeiKingdomInfo());
        if (detail.getShuKingdomInfo() != null)
            kingdoms.put(EnumKingdom.SHU, detail.getShuKingdomInfo());
        if (detail.getWuKingdomInfo() != null)
            kingdoms.put(EnumKingdom.WU, detail.getWuKingdomInfo());
    }

    public Match header() {
        return header;
    }

    public MatchDetail detail() {
        return detail;
    }

    public Map<EnumKingdom, KingdomInfo> kingdoms() {
        return kingdoms;
    }

    public void applyAll(List<? extends DomainEvent> events) {
        if (events == null)
            return;
        for (DomainEvent e : events)
            apply(e);
    }

    public void apply(DomainEvent e) {
        if (e == null)
            return;

        if (e instanceof GoldSpent ev) {
            KingdomInfo k = mustGet(ev.kingdom());
            k.setGold(dec(nz(k.getGold()), ev.amount()));
            return;
        }

        if (e instanceof TroopsRaised ev) {
            KingdomInfo k = mustGet(ev.kingdom());
            k.setTrainedTroops(inc(nz(k.getTrainedTroops()), ev.amount()));
            return;
        }
    }

    private KingdomInfo mustGet(EnumKingdom kg) {
        KingdomInfo info = kingdoms.get(kg);
        if (info == null)
            throw new IllegalStateException("KingdomInfo not loaded for " + kg);
        return info;
    }

    private static int nz(Integer v) {
        return v == null ? 0 : v;
    }

    private static int inc(int base, int by) {
        return base + by;
    }

    private static int dec(int base, int by) {
        int r = base - by;
        if (r < 0)
            throw new IllegalStateException("Negative resource after apply");
        return r;
    }
}
