package com.example.three_kingdom_backend.match.store;

import java.io.Serializable;
import java.util.Objects;

import lombok.Data;

@Data
public class MatchEventKey implements Serializable {
    private Long matchId;
    private Long seq;

    public MatchEventKey() {
    }

    public MatchEventKey(Long matchId, Long seq) {
        this.matchId = matchId;
        this.seq = seq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof MatchEventKey that))
            return false;
        return Objects.equals(matchId, that.matchId) && Objects.equals(seq, that.seq);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchId, seq);
    }
}
