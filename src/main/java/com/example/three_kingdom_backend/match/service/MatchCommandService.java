package com.example.three_kingdom_backend.match.service;

import com.example.three_kingdom_backend.match.Match;
import com.example.three_kingdom_backend.match.MatchDetail;
import com.example.three_kingdom_backend.match.KingdomInfo;
import com.example.three_kingdom_backend.match.commands.MatchCommand;
import com.example.three_kingdom_backend.match.engine.MatchAggregate;
import com.example.three_kingdom_backend.match.engine.Simulator;
import com.example.three_kingdom_backend.match.events.DomainEvent;
import com.example.three_kingdom_backend.match.events.GoldSpent;
import com.example.three_kingdom_backend.match.events.TroopsRaised;
import com.example.three_kingdom_backend.match.store.MatchEvent;
import com.example.three_kingdom_backend.match.store.MatchEventRepository;
import com.example.three_kingdom_backend.match.store.MatchEventTx;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * XỬ LÝ GHI (Command-side):
 * - Idempotency (match_event_tx)
 * - Load read-model hiện tại
 * - Decide -> List<DomainEvent> (chưa có seq)
 * - Persist header + gán seq + persist events
 * - Apply vào aggregate (mutate các entity đã load)
 * - Save read-model
 * - Trả CommandResult cho client
 */
@Service
@RequiredArgsConstructor
public class MatchCommandService {

    // Read-model repos (bạn đã/đang có sẵn)
    private final com.example.three_kingdom_backend.match.service.MatchRepository matchRepo;
    private final MatchDetailRepository matchDetailRepo;
    private final com.example.three_kingdom_backend.match.service.KingdomInfoRepository kingdomRepo;

    // Event-store repos
    private final MatchEventTxRepository txRepo;
    private final MatchEventRepository eventRepo;

    private final ObjectMapper objectMapper;

    @Transactional
    public CommandResult handle(MatchCommand cmd) {
        cmd.basicValidate(); // null checks cơ bản từ interface MatchCommand

        // 1) Idempotency: nếu client gửi idempotencyKey
        if (cmd.idempotencyKey() != null && !cmd.idempotencyKey().isBlank()) {
            var existed = txRepo.findByMatchIdAndIdempotencyKey(cmd.matchId(), cmd.idempotencyKey());
            if (existed.isPresent()) {
                // Đã xử lý trước đó -> trả trạng thái gọn (không nhân đôi hành động)
                long lastSeq = eventRepo.findMaxSeq(cmd.matchId());
                return new CommandResult(existed.get().getTxId(), lastSeq, List.of(), Map.of());
            }
        }

        // 2) Load read-model hiện tại (managed entities)
        Match match = matchRepo.findById(cmd.matchId())
                .orElseThrow(() -> new IllegalStateException("Match not found: " + cmd.matchId()));
        MatchDetail matchDetail = matchDetailRepo.findTopByMatchIdOrderByIdDesc(cmd.matchId())
                .orElseThrow(() -> new IllegalStateException("MatchDetail not found for match: " + cmd.matchId()));
        var agg = new MatchAggregate(match, matchDetail);

        // 3) Persist TX header trước để lấy txId (DB tự gán DEFAULT cho tx_id)
        MatchEventTx matchEventTx = new MatchEventTx();
        matchEventTx.setMatchId(cmd.matchId());
        matchEventTx.setCommandType(cmd.type().name());
        matchEventTx.setActorKingdom(cmd.actor());
        matchEventTx.setRoundNumber(matchDetail.getRoundNumber());
        matchEventTx.setPhase(matchDetail.getPhase() != null ? matchDetail.getPhase().name() : null);
        matchEventTx.setIdempotencyKey(cmd.idempotencyKey());
        // createdAt will be set by Auditable
        txRepo.save(matchEventTx);
        long txId = matchEventTx.getTxId(); // lấy từ DEFAULT nextval('match_event_tx_id_seq')

        // 4) Decide events (chưa có seq)
        List<DomainEvent> decidedEvents = Simulator.decide(agg, cmd, txId);

        // 5) Gán seq + persist từng event + apply aggregate
        long lastSeq = eventRepo.findMaxSeq(cmd.matchId());
        List<Map<String, Object>> evSummaries = new ArrayList<>(decidedEvents.size());

        for (DomainEvent rawEvent : decidedEvents) {
            long seq = ++lastSeq;
            DomainEvent event = assignSeq(rawEvent, seq); // rawEvent.withSeq(seq)
            Map<String, Object> payload = toPayload(event); // map -> JSON

            MatchEvent matchEventEntity = new MatchEvent();
            matchEventEntity.setMatchId(cmd.matchId());
            matchEventEntity.setSeq(seq);
            matchEventEntity.setTxId(txId);
            matchEventEntity.setType(event.type());
            matchEventEntity.setPayloadJson(serialize(payload));
            // createdAt will be set by Auditable
            eventRepo.save(matchEventEntity);

            // mutate in-memory on loaded entities
            agg.apply(event);

            // summarize for client
            evSummaries.add(Map.of(
                    "seq", seq,
                    "type", event.type(),
                    "data", payload));
        }

        // 6) Save read-model (can rely on dirty checking, but call save for clarity)
        kingdomRepo.saveAll(agg.kingdoms().values());
        matchDetailRepo.save(agg.detail());
        matchRepo.save(agg.header());

        // 7) Build view nhẹ (client dùng để cập nhật UI nhanh)
        Map<String, Object> view = buildMiniView(agg);

        // 8) Return (commit will happen when @Transactional method ends without
        // exceptions)
        return new CommandResult(txId, lastSeq, evSummaries, view);
    }

    // ---------- Helpers ----------

    private String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception exception) {
            throw new RuntimeException("Serialize payload failed", exception);
        }
    }

    private DomainEvent assignSeq(DomainEvent event, long seq) {
        if (event instanceof GoldSpent goldSpent)
            return goldSpent.withSeq(seq);
        if (event instanceof TroopsRaised troopsRaised)
            return troopsRaised.withSeq(seq);
        return event;
    }

    private Map<String, Object> toPayload(DomainEvent event) {
        if (event instanceof GoldSpent goldSpent) {
            return Map.of("kingdom", goldSpent.kingdom().name(), "amount", goldSpent.amount());
        }
        if (event instanceof TroopsRaised troopsRaised) {
            return Map.of("kingdom", troopsRaised.kingdom().name(), "amount", troopsRaised.amount());
        }
        return Map.of(); // expand when adding new event
    }

    private Map<String, Object> buildMiniView(MatchAggregate agg) {
        Map<String, Object> result = new HashMap<>();
        agg.kingdoms().forEach((k, info) -> {
            result.put(k.name(), Map.of(
                    "gold", nonNull(info.getGold()),
                    "trainedTroops", nonNull(info.getTrainedTroops())));
        });
        return result;
    }

    private static int nonNull(Integer value) {
        return value == null ? 0 : value;
    }
}
